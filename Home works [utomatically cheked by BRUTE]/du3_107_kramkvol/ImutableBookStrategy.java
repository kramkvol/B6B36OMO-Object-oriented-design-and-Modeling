package cvut.fel.services.strategy;

import cvut.fel.model.Book;

public class ImutableBookStrategy implements BookStrategy {

    @Override
    public Book update(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }

        return new Book(
                book.getId() + 1,
                book.getISBN(),
                book.getName()
        );
    }
}
