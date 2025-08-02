package com.example.mcp.service.impl;

import com.example.mcp.dto.book.BookRequestDto;
import com.example.mcp.model.Author;
import com.example.mcp.model.Book;
import com.example.mcp.repository.AuthorRepository;
import com.example.mcp.repository.BookRepository;
import com.example.mcp.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Override
    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book Not Found"));
    }

    @Override
    public List<Book> getBooksByAuthorId(Long authorId){
        return bookRepository.findAllBooksByAuthorId(authorId);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional
    public Book saveBook(BookRequestDto book) {
        Book bookEntity = new Book();
        bookEntity.setTitle(book.title());
        return bookRepository.save(bookEntity);
    }

    @Override
    @Transactional
    public Book updateBook(BookRequestDto book, Long id) {
        Book foundBook = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book Not Found"));
        foundBook.setTitle(book.title());
        return bookRepository.save(foundBook);
    }

    @Override
    @Transactional
    public boolean deleteBookById(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Book addAuthorToBook(Long bookId, Long authorId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book Not Found"));
        Author author = authorRepository.findById(authorId).orElseThrow(() -> new RuntimeException("Author Not Found"));
        
        book.getAuthors().add(author);
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book removeAuthorFromBook(Long bookId, Long authorId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book Not Found"));
        Author author = authorRepository.findById(authorId).orElseThrow(() -> new RuntimeException("Author Not Found"));
        
        book.getAuthors().remove(author);
        return bookRepository.save(book);
    }
}
