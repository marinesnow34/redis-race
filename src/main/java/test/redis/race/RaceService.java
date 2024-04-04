package test.redis.race;

import java.util.Collections;
import java.util.Random;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RaceService {
	private final OrderRepository orderRepository;
	private final RedisTemplate<String, String> redisTemplate;

	public Long incr() {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		Long orderNumber = valueOperations.increment("orderNumber", 1);
		Order order = new Order(orderNumber);
		return orderRepository.save(order).getOrderNumber();
	}

	public Long getSet() {
		Random random = new Random();
		int randomSeconds = 1 + random.nextInt(10);
		try {
			Thread.sleep(randomSeconds * 1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		Long orderNumber = Long.parseLong(valueOperations.get("orderNumber"));
		Long newOrderNumber = orderNumber + 1;
		valueOperations.set("orderNumber", newOrderNumber.toString());

		Order order = new Order(newOrderNumber);
		return orderRepository.save(order).getOrderNumber();
	}

	public Long getSetWithLuaScript() {
		String luaScript =
			"local current = redis.call('get', KEYS[1]) " +
				"if current == false then current = 0 end " +
				"current = current + 1 " +
				"redis.call('set', KEYS[1], current) " +
				"return current";

		// 스크립트 실행을 위한 DefaultRedisScript 객체 생성
		DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
		redisScript.setScriptText(luaScript);
		redisScript.setResultType(Long.class); // 반환 타입 지정

		// Lua 스크립트 실행
		Long newOrderNumber = redisTemplate.execute(redisScript, Collections.singletonList("orderNumber"));

		Order order = new Order(newOrderNumber);
		return orderRepository.save(order).getOrderNumber();
	}
}
