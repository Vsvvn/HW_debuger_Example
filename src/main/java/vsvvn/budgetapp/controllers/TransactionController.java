package vsvvn.budgetapp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vsvvn.budgetapp.model.Category;
import vsvvn.budgetapp.model.Transaction;
import vsvvn.budgetapp.services.BudgetService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Month;

@RestController
@RequestMapping("/transaction")
@Tag(name = "Транзакции", description = "CRUD-операции и другие эндпоинты для работы с транзакциями")
public class TransactionController {

    private final BudgetService budgetService;

    public TransactionController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<Long> addTransaction(@RequestBody Transaction transaction) {
        Long id = budgetService.addTransaction(transaction);
        return ResponseEntity.ok(id);
    }
//    .body(id)

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable long id) {
        Transaction transaction = budgetService.getTransaction(id);
        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(transaction);
    }

    @GetMapping
    @Operation(
            summary = "Поиск транзакций по месяцу и/или категории",
            description = "Можно искать по одному параметру, обоим или вообще без параметров"
    )
    @Parameters(value = {
            @Parameter(name = "month", example = "Декабрь")
    })

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Транзацкии были найдены",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Transaction.class))
                            )
                    }
            )
    })

    public ResponseEntity<Transaction> getAllTransactions(@RequestParam(required = false) Month month,
                                                          @RequestParam(required = false) Category category) {
        return null;
    }

    @GetMapping("/byMonth{month}")
    public ResponseEntity<Object> getTransactionsByMonth(@PathVariable Month month) {

        try {
            Path path = budgetService.createMonthlyReport(month);
            if (Files.size(path) == 0) {
                return ResponseEntity.noContent().build();
            }
            InputStreamResource resource = new InputStreamResource(new FileInputStream(path.toFile()));
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(Files.size(path))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + month + " -report.txt\"")
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.toString());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> editTransaction(@PathVariable long id, @RequestBody Transaction newTransaction) {
        Transaction transaction = budgetService.editTransaction(id, newTransaction);
        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(transaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable long id) {
        if (budgetService.deleteTransaction(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping

    public ResponseEntity<Void> deleteAllTransaction(@PathVariable long id) {
        budgetService.deleteAllTransaction();
        return ResponseEntity.ok().build();
    }
}
