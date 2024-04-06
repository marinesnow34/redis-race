package test.redis.race;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RaceServiceTest {
	@Autowired
	private RaceService raceService;
	@Autowired
	private org.springframework.data.redis.core.RedisTemplate<String, String> redisTemplate;

	@Test
	@DisplayName("레디스 INCR 테스트")
	void redis_INCR_test() throws InterruptedException {
		ConcurrentHashMap<Long, Long> orderNumbers = new ConcurrentHashMap<Long, Long>();
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		CountDownLatch countDownLatch = new CountDownLatch(100);

		for (int i = 0; i < 100; i++) {
			executorService.execute(() -> {
				try {
					orderNumbers.put(raceService.incr(), 0L);
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();
		assertEquals(100, orderNumbers.size());
	}

	@Test
	@DisplayName("레디스 GET SET 테스트")
	void redis_GET_SET_thread_test() throws InterruptedException {
		ConcurrentHashMap<Long, Long> orderNumbers = new ConcurrentHashMap<Long, Long>();
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		CountDownLatch countDownLatch = new CountDownLatch(100);

		for (int i = 0; i < 100; i++) {
			executorService.execute(() -> {
				try {
					orderNumbers.put(raceService.getSet(), 0L);
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();
		assertEquals(100, orderNumbers.size());
	}

	@Test
	@DisplayName("레디스 GET SET 단일 스레드 테스트")
	void redis_GET_SET_single_test(){
		ConcurrentHashMap<Long, Long> orderNumbers = new ConcurrentHashMap<Long, Long>();

		for (int i = 0; i < 100; i++) {
			orderNumbers.put(raceService.getSet(), 0L);
		}

		assertEquals(100, orderNumbers.size());
	}

	@Test
	@DisplayName("레디스 Lua 스크립트 테스트")
	void redis_lua_thread_test() throws InterruptedException {
		ConcurrentHashMap<Long, Long> orderNumbers = new ConcurrentHashMap<Long, Long>();
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		CountDownLatch countDownLatch = new CountDownLatch(100);

		for (int i = 0; i < 100; i++) {
			executorService.execute(() -> {
				try {
					orderNumbers.put(raceService.getSetWithLuaScript(), 0L);
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();
		assertEquals(100, orderNumbers.size());
	}

	@Test
	@DisplayName("레디스 sadd 스크립트 테스트")
	void redis_sadd_test() throws InterruptedException {
		ConcurrentHashMap<String, Long> orderNumbers = new ConcurrentHashMap<String, Long>();
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		CountDownLatch countDownLatch = new CountDownLatch(100);

		for (int i = 0; i < 100; i++) {
			executorService.execute(() -> {
				try {
					orderNumbers.put(raceService.sadd(), 0L);
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();
		assertEquals(100, orderNumbers.size());
	}
}