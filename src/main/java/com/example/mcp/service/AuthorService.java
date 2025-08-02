package com.example.mcp.service;

import com.example.mcp.dto.author.AuthorRequestDto;
import com.example.mcp.model.Author;

import java.util.List;

public interface AuthorService {
    Author getAuthorById(Long id);
    List<Author> getAuthorsByBook(Long bookId);
    List<Author> getAllAuthors();
    Author saveAuthor(AuthorRequestDto author);
    Author updateAuthor(AuthorRequestDto author, Long id);
    boolean deleteAuthorById(Long id);
    
    // Методи для управління зв'язками
    Author addBookToAuthor(Long authorId, Long bookId);
    Author removeBookFromAuthor(Long authorId, Long bookId);
}
