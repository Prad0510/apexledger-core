package com.example.apexledger_core.controller;

import com.example.apexledger_core.dto.TransferRequest;
import com.example.apexledger_core.exception.RateLimitExceededException;
import com.example.apexledger_core.service.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// Tells Spring that this class handles incoming web API requests.
@RestController

// Sets the base URL prefix. All links here start with /api/v1/ledger.
@RequestMapping("/api/v1/ledger")

// Customizes the main heading text visible at the top of Swagger UI page.
@Tag(name = "ApexLedger Core Engine",description = "High-concurrency transactional ledger endpoints protected by atomic distributed shield layers.")
public class LedgerController {

    // Automatically pull in the business logic service layer to use.
    @Autowired
    private LedgerService ledgerService;

    // Defines a POST web endpoint at /api/v1/ledger/transfer.
    @PostMapping("/transfer")

    //Gives endpoint a neat name and description inside the Swagger UI panel.
    @Operation(summary = "Execute Protected Ledger Transfer", description = "Processes an isolated transfer between accounts with deterministic deadlock avoidance and Redis rate-limiting guards.")

    // Maps out the exact error code boxes displayed on the Swagger dashboard.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction successfully committed and streamed to Kafka."),
            @ApiResponse(responseCode = "400", description = "Financial invariant violation or account validation failure."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded. Request intercepted by Redis token shield."),
            @ApiResponse(responseCode = "500", description = "Internal processing or streaming node sync error.")
    })

    // @RequestBody captures the incoming JSON data and maps it into the TransferRequest object.
    public ResponseEntity<?> transferFunds(@RequestBody TransferRequest request) {
        try {
            // Step 1: Pass the incoming account details and amount to the main ledger engine.
            ledgerService.executeTransfer(
                    request.getSourceAccountId(),
                    request.getDestinationAccountId(),
                    request.getAmount()
            );

            // Step 2: If everything succeeds, return an HTTP 200 OK with a success message.
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Funds transferred successfully."
            ));

        } catch (RateLimitExceededException e) {
            // Step 3a: Shield Layer Interception -> HTTP 429 Too Many Requests
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
                    "status", "BLOCKED",
                    "message", e.getMessage()
            ));

        } catch (IllegalArgumentException e) {
            // Step 3b: Validation or Account Not Found issues -> HTTP 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "FAILED",
                    "message", e.getMessage()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            // Step 3c: Fallback for unexpected failures -> HTTP 500 Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "ERROR",
                    "message", "An unexpected processing error occurred inside the core engine."
            ));
        }
    }
}
