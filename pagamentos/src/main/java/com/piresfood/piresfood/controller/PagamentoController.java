package com.piresfood.piresfood.controller;

import com.piresfood.piresfood.dto.PagamentoDto;
import com.piresfood.piresfood.service.PagamentoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("pagamentos")
//@EnableFeignClients
public class PagamentoController {

    @Autowired
    PagamentoService service;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping
    public ResponseEntity<Page<PagamentoDto>> buscarTodos(Pageable pageable) {
        var pagamentos = service.buscarTodos(pageable);
        return ResponseEntity.ok(pagamentos);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<PagamentoDto> buscarPorId(@PathVariable Long id) {
        var pagamento = service.buscarPorId(id);
        return ResponseEntity.ok(pagamento);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<PagamentoDto> cadastrar(@RequestBody @Valid PagamentoDto pagamentoDto, UriComponentsBuilder uriComponentsBuilder) {
        var pagamento = service.criar(pagamentoDto);
        var endereço = uriComponentsBuilder.path("/pagamentos/{id}").buildAndExpand(pagamento.getId()).toUri();

        rabbitTemplate.convertAndSend("pagamentos.ex", "", pagamento);

        return ResponseEntity.created(endereço).body(pagamento);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<PagamentoDto> atualizar(@PathVariable Long id, @RequestBody @Valid PagamentoDto pagamentoDto) {
        var pagamento = service.atualizar(id, pagamentoDto);
        return ResponseEntity.ok(pagamento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

//    @PatchMapping("/{id}/confirmar")
//    @CircuitBreaker(name = "atualizaPedido", fallbackMethod = "pagamentoAutorizadoComIntegracaoPendente")
//    public void confirmarPagamento(@PathVariable @NotNull Long id) {
//        service.confirmarPagamento(id);
//    }

//    public void pagamentoAutorizadoComIntegracaoPendente(Long id, Exception e){
//        service.alteraStatus(id);
//    }

}
