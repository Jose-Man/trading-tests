package name.lattuada.trading.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import name.lattuada.trading.model.Mapper;
import name.lattuada.trading.model.dto.SecurityDTO;
import name.lattuada.trading.model.entities.SecurityEntity;
import name.lattuada.trading.repository.ISecurityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/securities")
public class SecurityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityController.class);
    private static final String EXCEPTION_CAUGHT = "Exception caught";

    private final ISecurityRepository securityRepository;

    @Autowired
    private SecurityController(ISecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }

    @GetMapping()
    @ApiOperation(value = "Get list of securities",
            notes = "Returns a list of securities")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No securities"),
            @ApiResponse(code = 500, message = "Server error")
    })
    public ResponseEntity<List<SecurityDTO>> getSecurities() {
        try {
            List<SecurityEntity> securityList = securityRepository.findAll();
            if (securityList.isEmpty()) {
                LOGGER.info("No securities found");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found {} securities: {}", securityList.size(), securityList);
            } else {
                LOGGER.info("Found {} securities", securityList.size());
            }
            return new ResponseEntity<>(Mapper.mapAll(securityList, SecurityDTO.class), HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(EXCEPTION_CAUGHT, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{id}")
    @ApiOperation(value = "Get a specific security",
            notes = "Returns specific security given its id")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Security not found"),
            @ApiResponse(code = 500, message = "Server error")
    })
    public ResponseEntity<SecurityDTO> getSecurityById(@PathVariable("id") UUID uuid) {
        try {
            Optional<SecurityEntity> optSecurity = securityRepository.findById(uuid);
            return optSecurity.map(security -> {
                LOGGER.info("Security found having id: {}", uuid);
                return new ResponseEntity<>(Mapper.map(security, SecurityDTO.class), HttpStatus.OK);
            }).orElseGet(() -> {
                LOGGER.info("No security found having id {}", uuid);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            });
        } catch (Exception e) {
            LOGGER.error(EXCEPTION_CAUGHT, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create security",
            notes = "Create a new security. Note: its ID is not mandatory, but it will be automatically generated")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Security created"),
            @ApiResponse(code = 500, message = "Server error")
    })
    public ResponseEntity<SecurityDTO> addSecurity(@Valid @RequestBody SecurityDTO security) {
        try {
            SecurityEntity created = securityRepository.save(Mapper.map(security, SecurityEntity.class));
            LOGGER.info("Added security: {}", created);
            return new ResponseEntity<>(Mapper.map(created, SecurityDTO.class), HttpStatus.CREATED);
        } catch (Exception e) {
            LOGGER.error(EXCEPTION_CAUGHT, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
