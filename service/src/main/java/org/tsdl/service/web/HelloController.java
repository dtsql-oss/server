package org.tsdl.service.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
@Tag(name = "\"Hello, World\" Endpoint", description = "Sample endpoint")
public class HelloController {
    /*private final StorageService storageService;*/

    @Autowired
    public HelloController(/*StorageService storageService*/) {
        /*this.storageService = storageService;*/
    }

    @GetMapping
    @Operation(summary = "Retrieve greeting")
    @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Greeting was conducted successfully."),
    })
    public String world() {
        return "Hello, World!";
    }

    @GetMapping("fail")
    @Operation(summary = "Causes internal server error")
    @ApiResponses({
      @ApiResponse(responseCode = "500", description = "Endpoint invocation caused internal server error.")
    })
    public String fail() {
        //storageService.get("", 0, "");
        return "unreachable";
    }
}
