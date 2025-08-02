package com.example.mcp.controller;

import com.example.mcp.dto.author.AuthorResponseDto;
import com.example.mcp.model.Author;
import com.example.mcp.model.Book;
import com.example.mcp.service.AuthorService;
import com.example.mcp.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class QueryController {

    private final AuthorService authorService;
    private final BookService bookService;

    @QueryMapping
    @Tool(description = "Get author by ID")
    public Author getAuthor(@ToolParam(description = "Author ID") @Argument Long id) {
        return authorService.getAuthorById(id);

    }

    @QueryMapping
    @Tool(description = "Get book by ID")
    public Book getBook(@ToolParam(description = "Book ID") @Argument Long id) {
        return bookService.getBookById(id);
    }

    @QueryMapping
    @Tool(description = "List all authors")
    public List<Author> listAuthors() {
        return authorService.getAllAuthors();
    }

    @QueryMapping
    @Tool(description = "List all books")
    public List<Book> listBooks() {
        return bookService.getAllBooks();
    }

    @QueryMapping
    @Tool(description = "Get books by author ID")
    public List<Book> getBooksByAuthor(@ToolParam(description = "Author ID") @Argument Long authorId) {
        return bookService.getBooksByAuthorId(authorId);
    }

    @QueryMapping
    @Tool(description = "Get authors by book ID")
    public List<Author> getAuthorsByBook(@ToolParam(description = "Book ID") @Argument Long bookId) {
        return authorService.getAuthorsByBook(bookId);
    }
}
