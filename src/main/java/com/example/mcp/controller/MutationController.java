package com.example.mcp.controller;

import com.example.mcp.dto.author.AuthorRequestDto;
import com.example.mcp.dto.book.BookRequestDto;
import com.example.mcp.model.Author;
import com.example.mcp.model.Book;
import com.example.mcp.service.AuthorService;
import com.example.mcp.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MutationController {

    private final AuthorService authorService;
    private final BookService bookService;

    @MutationMapping
    @Tool(description = "Create and save new author")
    public Author createAuthor(
            @ToolParam(description = "Author's first name") @Argument String firstName,
            @ToolParam(description = "Author's last name") @Argument String lastName) {
        return authorService.saveAuthor(new AuthorRequestDto(firstName, lastName));
    }

    @MutationMapping
    @Tool(description = "Update existing author")
    public Author updateAuthor(
            @ToolParam(description = "Author ID") @Argument Long id,
            @ToolParam(description = "Author's first name") @Argument String firstName,
            @ToolParam(description = "Author's last name") @Argument String lastName) {
        return authorService.updateAuthor(new AuthorRequestDto(firstName, lastName), id);
    }

    @MutationMapping
    @Tool(description = "Delete author by ID")
    public Boolean deleteAuthor(@ToolParam(description = "Author ID") @Argument Long id) {
        return authorService.deleteAuthorById(id);
    }

    @MutationMapping
    @Tool(description = "Create and save new book")
    public Book createBook(@ToolParam(description = "Book title") @Argument String title) {
        return bookService.saveBook(new BookRequestDto(title));
    }

    @MutationMapping
    @Tool(description = "Update existing book")
    public Book updateBook(
            @ToolParam(description = "Book ID") @Argument Long id,
            @ToolParam(description = "Book title") @Argument String title) {
        return bookService.updateBook(new BookRequestDto(title), id);
    }

    @MutationMapping
    @Tool(description = "Delete book by ID")
    public Boolean deleteBook(@ToolParam(description = "Book ID") @Argument Long id) {
        return bookService.deleteBookById(id);
    }

    @MutationMapping
    @Tool(description = "Add author to book")
    public Book addAuthorToBook(
            @ToolParam(description = "Book ID") @Argument Long bookId,
            @ToolParam(description = "Author ID") @Argument Long authorId) {
        return bookService.addAuthorToBook(bookId, authorId);
    }

    @MutationMapping
    @Tool(description = "Remove author from book")
    public Book removeAuthorFromBook(
            @ToolParam(description = "Book ID") @Argument Long bookId,
            @ToolParam(description = "Author ID") @Argument Long authorId) {
        return bookService.removeAuthorFromBook(bookId, authorId);
    }

    @MutationMapping
    @Tool(description = "Add book to author")
    public Author addBookToAuthor(
            @ToolParam(description = "Author ID") @Argument Long authorId,
            @ToolParam(description = "Book ID") @Argument Long bookId) {
        return authorService.addBookToAuthor(authorId, bookId);
    }

    @MutationMapping
    @Tool(description = "Remove book from author")
    public Author removeBookFromAuthor(
            @ToolParam(description = "Author ID") @Argument Long authorId,
            @ToolParam(description = "Book ID") @Argument Long bookId) {
        return authorService.removeBookFromAuthor(authorId, bookId);
    }
}
