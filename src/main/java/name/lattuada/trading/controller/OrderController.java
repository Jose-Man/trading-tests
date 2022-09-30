package name.lattuada.trading.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import name.lattuada.trading.model.EOrderType;
import name.lattuada.trading.model.Mapper;
import name.lattuada.trading.model.dto.OrderDTO;
import name.lattuada.trading.model.entities.OrderEntity;
import name.lattuada.trading.model.entities.TradeEntity;
import name.lattuada.trading.repository.IOrderRepository;
import name.lattuada.trading.repository.ITradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);
    private static final String EXCEPTION_CAUGHT = "Exception caught";

    private final IOrderRepository orderRepository;
    private final ITradeRepository tradeRepository;

    @Autowired
    public OrderController(IOrderRepository orderRepository, @Lazy ITradeRepository tradeRepository) {
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
    }

    @GetMapping()
    @ApiOperation(value = "Get list of orders",
            notes = "Returns a list of orders")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No orders"),
            @ApiResponse(code = 500, message = "Server error")
    })
    public ResponseEntity<List<OrderDTO>> getOrders() {
        try {
            List<OrderEntity> orderList = orderRepository.findAll();
            if (orderList.isEmpty()) {
                LOGGER.info("No orders found");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found {} orders: {}", orderList.size(), orderList);
            } else {
                LOGGER.info("Found {} orders", orderList.size());
            }
            return new ResponseEntity<>(Mapper.mapAll(orderList, OrderDTO.class), HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(EXCEPTION_CAUGHT, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{id}")
    @ApiOperation(value = "Get a specific order",
            notes = "Returns specific order given its id")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Order not found"),
            @ApiResponse(code = 500, message = "Server error")
    })
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable("id") UUID uuid) {
        try {
            Optional<OrderEntity> optOrder = orderRepository.findById(uuid);
            return optOrder.map(order -> {
                LOGGER.info("Order found having id: {}", uuid);
                return new ResponseEntity<>(Mapper.map(order, OrderDTO.class), HttpStatus.OK);
            }).orElseGet(() -> {
                LOGGER.info("No order found having id {}", uuid);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            });
        } catch (Exception e) {
            LOGGER.error(EXCEPTION_CAUGHT, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create order",
            notes = "Create a new order. Note: its ID is not mandatory, but it will be automatically generated")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Order created"),
            @ApiResponse(code = 500, message = "Server error")
    })
    public ResponseEntity<OrderDTO> addOrder(@Valid @RequestBody OrderDTO order) {
        try {
            OrderEntity created = orderRepository.save(Mapper.map(order, OrderEntity.class));
            created = orderRepository.getById(created.getId());
            LOGGER.info("Added order: {}", created);
            //
            List<OrderEntity> relatedOrders = orderRepository.findBySecurityIdAndTypeAndFulfilled(created.getSecurityId(),
                    EOrderType.BUY == created.getType() ? EOrderType.SELL : EOrderType.BUY,
                    false);
            manageTrading(created, relatedOrders);
            //
            return new ResponseEntity<>(Mapper.map(created, OrderDTO.class), HttpStatus.CREATED);
        } catch (Exception e) {
            LOGGER.error(EXCEPTION_CAUGHT, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void manageTrading(OrderEntity created, List<OrderEntity> relatedOrders) {
        if (!relatedOrders.isEmpty()) {
            LOGGER.info("Found related order(s). Created: {}; related found: {}", created, relatedOrders);
            if (EOrderType.BUY == created.getType()) {
                // I created a BUY order and now I found all related SELL order(s)
                // Find the one with min price
                OrderEntity related = orderRepository.getById(relatedOrders.stream()
                        .min(Comparator.comparing(OrderEntity::getPrice))
                        .orElseThrow(NoSuchElementException::new).getId());
                if (created.getPrice() >= related.getPrice()) {
                    createTrade(created, related);
                }
            } else {
                // I created a SELL order and now I found all related BUY order(s)
                // For sake of simplicity, I take the first one
                OrderEntity related = orderRepository.getById(relatedOrders.get(0).getId());
                if (created.getPrice() <= related.getPrice()) {
                    createTrade(related, created);
                }
            }
        } else {
            LOGGER.debug("No unfulfilled related orders found for securityId {}", created.getSecurityId());
        }
    }

    private void createTrade(OrderEntity buy, OrderEntity sell) {
        LOGGER.debug("It's time to trade between buy {} and sell {}", buy, sell);
        // Create trade
        TradeEntity trade = new TradeEntity();
        trade.setPrice(sell.getPrice());
        trade.setQuantity(buy.getQuantity());
        trade.setOrderSellId(sell.getId());
        trade.setOrderBuyId(buy.getId());
        trade = tradeRepository.save(trade);
        LOGGER.info("Trade has been created: {}", trade);
        // Update orders
        buy.setFulfilled(Boolean.TRUE);
        sell.setFulfilled(Boolean.TRUE);
        sell = orderRepository.save(sell);
        LOGGER.info("Sell order has been updated: {}", sell);
        buy = orderRepository.save(buy);
        LOGGER.info("Buy order has bee updated: {}", buy);
    }

}
