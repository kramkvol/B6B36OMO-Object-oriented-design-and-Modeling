package cvut.fel.facade;

import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import cvut.fel.facade.dto.BookDto;
import cvut.fel.services.AuthorServiceImpl;
import cvut.fel.services.BookServiceImpl;
import cvut.fel.services.LibraryServiceImpl;
import cvut.fel.model.Book;
import cvut.fel.model.Author;
import cvut.fel.model.Library;
public class BookFacadeImpl implements BookFacade{

    private final AuthorServiceImpl authorServiceImpl;
    private final BookServiceImpl bookServiceImpl;
    private final LibraryServiceImpl libraryServiceImpl;

    public BookFacadeImpl(AuthorServiceImpl authorServiceImpl, BookServiceImpl bookServiceImpl, LibraryServiceImpl libraryServiceImpl) {
        this.authorServiceImpl = authorServiceImpl;
        this.bookServiceImpl = bookServiceImpl;
        this.libraryServiceImpl = libraryServiceImpl;
    }

    public BookFacadeImpl() {
        this.authorServiceImpl = new AuthorServiceImpl();
        this.bookServiceImpl = new BookServiceImpl();
        this.libraryServiceImpl = new LibraryServiceImpl();
    }
    @Override
    public BookDto getByBookId(int id) {
        Book book = bookServiceImpl.getByBookId(id);
        if (book == null) {
            throw new IllegalArgumentException("Book with ID " + id + " not found.");
        }

        Author author = authorServiceImpl.getByBookId(id);
        String authorName;
        if (author != null) {
            authorName = author.getFirstname() != null ? author.getFirstname() : "Unknown";
        } else {
            authorName = "Unknown";
        }

        Library library = libraryServiceImpl.getByBookId(id);
        String libraryName;
        if (library != null) {
            libraryName = (library.getName() != null) ? library.getName() : "Unknown Library";
        } else {
            libraryName = "Unknown Library";
        }
        return new BookDto(book.getName(), authorName, libraryName);
    }
}
