package ubu.gii.dass.c01;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class ReusablePoolTest {

	@BeforeAll
	public static void setUp() {
	}

	@AfterAll
	public static void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link ubu.gii.dass.c01.ReusablePool#getInstance()}.
	 */
	@Test
	@DisplayName("testGetInstance")
	public void testGetInstance() {
		ReusablePool pool1 = ReusablePool.getInstance();
		ReusablePool pool2 = ReusablePool.getInstance();
		assertNotNull(pool1);
		assertNotNull(pool2);
		assertSame(pool1, pool2);
		assertEquals(pool1, pool2);
	}

	/**
	 * Test method for {@link ubu.gii.dass.c01.ReusablePool#acquireReusable()}.
	 */
	@Test
	@DisplayName("testAcquireReusable")
	public void testAcquireReusable() {
		ReusablePool pool = ReusablePool.getInstance();
		for (int i = 0; i < 2; i++) {
			assertDoesNotThrow(() -> pool.acquireReusable());
		}

		assertThrows(NotFreeInstanceException.class, () -> {
			pool.acquireReusable();
		}, "Se esperaba NotFreeInstanceException al exceder el límite");
	}

	/**
	 * Test method for {@link ubu.gii.dass.c01.ReusablePool#releaseReusable(ubu.gii.dass.c01.Reusable)}.
	 */
	@Test
	@DisplayName("testReleaseReusable")
	public void testReleaseReusable() {
		ReusablePool pool = ReusablePool.getInstance();
		Reusable reusable = null;

		try {
			reusable = pool.acquireReusable();
		} catch (NotFreeInstanceException e) {
			fail("No se esperaba excepción al adquirir reusable");
		}

		Reusable finalReusable = reusable;
		assertDoesNotThrow(() -> pool.releaseReusable(finalReusable));

		assertThrows(DuplicatedInstanceException.class, () -> {
			pool.releaseReusable(finalReusable);
		}, "No se debe permitir liberar la misma instancia dos veces");
	}


	/**
	 * Test method for {@link ubu.gii.dass.c01.ReusablePool#acquireReusable()} and {@link ubu.gii.dass.c01.ReusablePool#releaseReusable(ubu.gii.dass.c01.Reusable)}.
	 */
	@Test
	@DisplayName("testPoolReuseAndUtil")
	public void testPoolReuseAndUtil() {
		ReusablePool pool = ReusablePool.getInstance();

		try {
			Reusable objA = pool.acquireReusable();
			Reusable objB = pool.acquireReusable();

			assertNotNull(objA, "objA no debe ser nulo");
			assertNotNull(objB, "objB no debe ser nulo");
			assertNotSame(objA, objB, "objA y objB deben ser distintos");

			String valA = objA.util();
			String valB = objB.util();

			assertTrue(valA.contains(String.valueOf(objA.hashCode())), 
				"valA debe contener hashCode de objA");
			assertTrue(valB.contains(":Uso del objeto Reutilizable"), 
				"valB debe contener el mensaje esperado");

			pool.releaseReusable(objB);
			pool.releaseReusable(objA);

			Reusable objC = pool.acquireReusable();
			Reusable objD = pool.acquireReusable();

			assertSame(objA, objC, "objC debe ser el mismo que objA");
			assertSame(objB, objD, "objD debe ser el mismo que objB");

			pool.releaseReusable(objC);
			pool.releaseReusable(objD);

		} catch (Exception e) {
			fail("Error inesperado: " + e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link ubu.gii.dass.c01.Client}.
	 */
	@Test
	@DisplayName("testClient")
	public void testClient() {
		assertNotNull(new Client());
		assertDoesNotThrow(() -> Client.main(null));
	}
}