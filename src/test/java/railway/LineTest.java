package railway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LineTest {

	private Station s1, s2, s3;
	private Line instance;

	@BeforeEach
	void setUp() {
		s1 = new Station("s1");
		s2 = new Station("s2");
		s3 = new Station("s3");
		ArrayList<Station> stations = new ArrayList<>();
		stations.add(s1);
		stations.add(s2);
		stations.add(s3);
		instance = new Line("line", stations);
	}

	@Test
	void getCurrentStation() {
		assertEquals(s1, instance.getCurrentStation());
	}

	@Test
	void isAtLastStation() {
		assertFalse(instance.isAtLastStation());
		instance.reachedNextStation();
		instance.reachedNextStation();
		assertTrue(instance.isAtLastStation());
		instance.reachedNextStation();
		assertFalse(instance.isAtLastStation());
	}

	@Test
	void getNextStation() {
		assertEquals(s2, instance.getNextStation());
	}

	@Test
	void reachedNextStation() {
		instance.reachedNextStation();
		assertEquals(s2, instance.getCurrentStation());
		assertEquals(s3, instance.getNextStation());
		instance.reachedNextStation();
		assertEquals(s3, instance.getCurrentStation());
		assertEquals(s1, instance.getNextStation());
		instance.reachedNextStation();
		assertEquals(s1, instance.getCurrentStation());
		assertEquals(s2, instance.getNextStation());
		instance.reachedNextStation();
		assertEquals(s2, instance.getCurrentStation());
		assertEquals(s3, instance.getNextStation());
	}
}