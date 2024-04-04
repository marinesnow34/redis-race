package test.redis.race;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class RaceController {
	private final RaceService raceService;
	@GetMapping("/")
	public String index() {
		return "Hello, World!";
	}
}
