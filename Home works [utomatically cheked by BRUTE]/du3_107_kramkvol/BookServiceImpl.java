package cvut.fel.services;

import cvut.fel.dao.BookRepository;
import cvut.fel.model.Book;
import cvut.fel.services.strategy.BookStrategy;

public class BookServiceImpl {
    BookRepository bookRepository = new BookRepository();
    private BookStrategy bookStrategy;

    public void setBookStrategy(BookStrategy bookStrategy) {
        if (bookStrategy == null) {
            throw new IllegalArgumentException("BookStrategy cannot be null.");
        }
        this.bookStrategy = bookStrategy;
    }


    public Book getByBookId(int bookId) {
        if (bookId <= 0) {
            System.err.println("Invalid book ID: " + bookId);
            return null;
        }

        Book book = bookRepository.getById(bookId);
        if (book == null) { System.err.println("Book with ID " + bookId + " not found.");}
        return book;
    }

    public Book getByBookName(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.err.println("Invalid book name.");
            return null;
        }
        Book book = bookRepository.getByName(name);
        if (book == null) { System.err.println("Book with name \"" + name + "\" not found.");}
        return book;
    }

    public Book updateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }

        if (bookStrategy == null) {
            throw new IllegalStateException("BookStrategy is not set.");
        }

        Book updatedBook = bookStrategy.update(book);

        if (updatedBook == null) {
            throw new IllegalStateException("Book update failed. Strategy returned null.");
        }

        return updatedBook;
    }
}
