package dev.akif.library.author;

import dev.akif.crud.CRUDController;
import dev.akif.crud.common.Paged;
import dev.akif.crud.common.Parameters;
import dev.akif.library.authorbook.AuthorBookService;
import dev.akif.library.authorbook.AuthorWithBooks;
import dev.akif.library.book.BookDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RequestMapping("/authors")
@RestController
@Tag(name = "Authors", description = "CRUD operations for authors")
public class AuthorController extends CRUDController<
        UUID,
        AuthorEntity,
        AuthorWithBooks,
        AuthorWithBooksDTO,
        CreateAuthor,
        UpdateAuthor,
        CreateAuthorDTO,
        UpdateAuthorDTO,
        AuthorMapper,
        AuthorDTOMapper,
        AuthorRepository,
        AuthorService> {
    private final AuthorBookService authorBookService;

    private static final String LIST_BOOKS_SUMMARY = "List books of author";
    private static final String LIST_BOOKS_DESCRIPTION = "List books of author with given pagination.";
    private static final String LIST_BOOKS_RESPONSE = "Books of author are returned successfully.";

    public AuthorController(final AuthorService service, final AuthorDTOMapper mapper, final AuthorBookService authorBookService) {
        super("Author", service, mapper);
        this.authorBookService = authorBookService;
    }

    @ApiResponse(responseCode = CODE_OK, description = LIST_BOOKS_RESPONSE)
    @GetMapping(path = "/{id}/books", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = LIST_BOOKS_SUMMARY, description = LIST_BOOKS_DESCRIPTION)
    public Paged<BookDTO> listBooksOfAuthor(
            @Parameter(name = "id", in = ParameterIn.PATH, description = GET_ID_DESCRIPTION)
            @PathVariable("id")
            final UUID authorId,
            @Parameter(name = "page", description = PAGE_DESCRIPTION)
            @RequestParam(name = "page", required = false, defaultValue = "0")
            final int page,
            @Parameter(name = "perPage", description = PER_PAGE_DESCRIPTION)
            @RequestParam(name = "perPage", required = false, defaultValue = "20")
            final int perPage,
            @Parameter(hidden = true)
            @PathVariable
            final Map<String, String> pathVariables,
            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        final var parameters = new Parameters(pathVariables, request);
        final var pageRequest = PageRequest.of(page, perPage);
        return authorBookService
                .listBooksOfAuthor(authorId, pageRequest, parameters)
                .map(b -> getMapper().bookToBookDTO(b));
    }
}
