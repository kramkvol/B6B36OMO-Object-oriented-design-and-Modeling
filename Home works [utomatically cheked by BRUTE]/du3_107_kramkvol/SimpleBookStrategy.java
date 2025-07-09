package cvut.fel.services.strategy;

import cvut.fel.model.Book;

public class SimpleBookStrategy implements BookStrategy {

    @Override
    public Book update(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }
        book.setName(book.getName());
        return book;
    }
}
