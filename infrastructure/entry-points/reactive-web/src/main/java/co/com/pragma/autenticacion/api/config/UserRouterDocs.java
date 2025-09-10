package co.com.pragma.autenticacion.api.config;

import co.com.pragma.autenticacion.api.UserHandler;
import co.com.pragma.autenticacion.api.dto.request.UserRequestDTO;
import co.com.pragma.autenticacion.api.dto.response.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static co.com.pragma.autenticacion.api.docs.UserApiDocs.*;

@Configuration
public class UserRouterDocs {

    private final RouterFunction<ServerResponse> userRoutes;

    public UserRouterDocs(@Qualifier("userRoutes") RouterFunction<ServerResponse> userRoutes) {
        this.userRoutes = userRoutes;
    }

    @Bean("userRoutesOpenApi")
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    produces = { "application/json" },
                    method = RequestMethod.POST,
                    beanClass = UserHandler.class,
                    beanMethod = "registerUser",
                    operation = @Operation(
                            operationId = "registerUser",
                            tags = { TAG },
                            security = { @SecurityRequirement(name = "bearerAuth") },
                            summary = REG_SUMMARY,
                            description = REG_DESC,
                            requestBody = @RequestBody(required = true, content = @Content(
                                    schema = @Schema(implementation = UserRequestDTO.class)
                            )),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Creado",
                                            content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                                    @ApiResponse(responseCode = "409", description = "Conflicto por duplicados"),
                                    @ApiResponse(responseCode = "422", description = "Regla de negocio violada")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/listar",
                    produces = { "application/json" },
                    method = RequestMethod.GET,
                    beanClass = UserHandler.class,
                    beanMethod = "listUsers",
                    operation = @Operation(
                            operationId = "listUsers",
                            tags = { TAG },
                            security = { @SecurityRequirement(name = "bearerAuth") },
                            summary = LIST_SUMMARY,
                            description = LIST_DESC,
                            responses = @ApiResponse(responseCode = "200",
                                    description = "OK",
                                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class)))
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/documento/{documentNumber}",
                    produces = { "application/json" },
                    method = RequestMethod.GET,
                    beanClass = UserHandler.class,
                    beanMethod = "getUserByDocumentNumber",
                    operation = @Operation(
                            operationId = "getUserByDocumentNumber",
                            tags = { TAG },
                            security = { @SecurityRequirement(name = "bearerAuth") },
                            summary = GET_BY_DOC_SUMMARY,
                            description = GET_BY_DOC_DESC,
                            parameters = {
                                    @Parameter(name = "documentNumber", in = ParameterIn.PATH, required = true,
                                            description = "Número de documento del usuario")
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "OK",
                                            content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
                                    @ApiResponse(responseCode = "404", description = "No encontrado")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> userRoutesOpenApi() {
        return userRoutes;
    }
}