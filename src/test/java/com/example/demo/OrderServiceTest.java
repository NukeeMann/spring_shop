package com.example.demo;

import net.stawrul.model.Book;
import net.stawrul.model.CD;
import net.stawrul.model.Movie;
import net.stawrul.model.Order;

import net.stawrul.services.OrdersService;
import net.stawrul.services.exceptions.*;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

	@Mock
	EntityManager em;

	@Test(expected = OutOfStockException.class)
	public void whenOrderedMovieIsNotAvailable_placeOrderThrowsOutOfStockException() {
		//Arrange
		Order order = new Order();
		Movie movie = new Movie();
		movie.setAmount(0);
		order.getMovies().add(movie);
		Mockito.when(em.find(Movie.class, movie.getId())).thenReturn(movie);
		OrdersService ordersService = new OrdersService(em);

		//Act
		ordersService.placeOrder(order);

		//Assert - exception expected
		Mockito.verify(em, never()).persist(order);
	}

	@Test(expected = ReduplicationException.class)
	public void whenOrderedTheSameProductTwice_placeOrderThrowsReduplicationException() {
		//Arrange
		Order order = new Order();
		Book book = new Book();
		book.setAmount(2);
		order.getBooks().add(book);
		order.getBooks().add(book);
		Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);
		OrdersService ordersService = new OrdersService(em);

		//Act
		ordersService.placeOrder(order);

		//Assert - exception expected
		Mockito.verify(em, never()).persist(order);
	}

	@Test(expected = NotExistException.class)
	public void whenOrderedUnavailableProdcut_placeOrderThrowsNotExistException() {
		//Arrange
		Order order = new Order();
		Book book = new Book();
		order.getBooks().add(book);
		Mockito.when(em.find(Book.class, book.getId())).thenReturn(null);
		OrdersService ordersService = new OrdersService(em);

		//Act
		ordersService.placeOrder(order);

		//Assert - exception expected
		Mockito.verify(em, never()).persist(order);
	}

	@Test(expected = EmptyOrderException.class)
	public void whenPlacedOrderIsEmpty_placeOrderEmptyOrderException() {
		Order order = new Order();
		OrdersService ordersService = new OrdersService(em);

		//Act
		ordersService.placeOrder(order);

		//Assert - exception expected
	}

	@Test(expected = MoviesAndCDsException.class)
	public void whenOrderedMovieAndCD_placeOrderThrowsMovieAndCDsException() {
		//Arrange
		Order order = new Order();
		Movie movie = new Movie();
		CD cd = new CD();
		order.getMovies().add(movie);
		order.getCDs().add(cd);

		OrdersService ordersService = new OrdersService(em);

		//Act
		ordersService.placeOrder(order);

		//Assert - exception expected
		Mockito.verify(em, never()).persist(order);
	}

	@Test
	public void whenOrderIsGood_placeOrder() {
		// Arrange:
		Book book = new Book();
		book.setAmount(10);
		book.setPrice(14.32);

		Order order = new Order();
		order.getBooks().add(book);

		Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);
		OrdersService ordersService = new OrdersService(em);

		// Act:
		ordersService.placeOrder(order);

		// Assert:
		Mockito.verify(em, times(1)).persist(order);
	}

	@Test
	public void whenOrderedMovieAvailable_placeOrderAndDecreasesAmount() {
		// Arrange:
		Movie movie = new Movie();
		movie.setAmount(10);    // Liczba zamowionych filmow

		// W sklepie bedzie 100 dostepnych egzemplarzy
		Movie copyMovie = new Movie();
		copyMovie.setAmount(100);
		copyMovie.setPrice(12.34);

		Order order = new Order();
		order.getMovies().add(movie);

		Mockito.when(em.find(Movie.class, movie.getId())).thenReturn(copyMovie);

		OrdersService ordersService = new OrdersService(em);

		// Act:
		ordersService.placeOrder(order);

		// Assert:
		assertEquals(90, (int)copyMovie.getAmount());
		Mockito.verify(em, times(1)).persist(order);
	}

	@Test
	public void whenOrderIsGood_placeOrderAndCountTotalPrice() {
		// Arrange:
		CD cd = new CD();
		cd.setAmount(10);    // Liczba zamowionych plyt
		cd.setPrice(12.30);

		Book book = new Book();
		book.setAmount(10);    // Liczba zamowionych filmow
		book.setPrice(7.70);

		Order order = new Order();
		order.getCDs().add(cd);
		order.getBooks().add(book);

		Mockito.when(em.find(CD.class, cd.getId())).thenReturn(cd);
		Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);

		OrdersService ordersService = new OrdersService(em);

		// Act:
		ordersService.placeOrder(order);

		// Assert:
		assertEquals(200.00, (double)order.getTotalPrice(), 0);
		Mockito.verify(em, times(1)).persist(order);
	}
}
