package com.example.mcp.service;

import com.example.mcp.dto.book.BookRequestDto;
import com.example.mcp.model.Book;

import java.util.List;

public interface BookService {
    Book getBookById(Long id);
    List<Book> getBooksByAuthorId(Long authorId);
    List<Book> getAllBooks();
    Book saveBook(BookRequestDto book);
    Book updateBook(BookRequestDto book, Long id);
    boolean deleteBookById(Long id);
    
    // Методи для управління зв'язками
    Book addAuthorToBook(Long bookId, Long authorId);
    Book removeAuthorFromBook(Long bookId, Long authorId);
}
