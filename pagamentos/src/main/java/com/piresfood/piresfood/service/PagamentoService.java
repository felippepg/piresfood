package com.piresfood.piresfood.service;


import com.piresfood.piresfood.dto.PagamentoDto;
//import com.piresfood.piresfood.http.PedidoClient;
import com.piresfood.piresfood.model.Pagamento;
import com.piresfood.piresfood.model.Status;
import com.piresfood.piresfood.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PagamentoService {
    @Autowired
    PagamentoRepository repository;

    @Autowired
    ModelMapper modelMapper;

//    @Autowired
//    PedidoClient pedido;

    public Page<PagamentoDto> buscarTodos(Pageable pageable) {
        return repository
                .findAll(pageable)
                .map(pagamento -> modelMapper.map(pagamento, PagamentoDto.class));
    }

    public PagamentoDto buscarPorId(Long id) {
        Pagamento pagamento =  repository
                .findById(id)
                .orElseThrow(EntityNotFoundException::new);

        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    public PagamentoDto criar(PagamentoDto pagamentoDto) {
        var pagamento = modelMapper.map(pagamentoDto, Pagamento.class);
        pagamento.setStatus(Status.CRIADO);
        repository.save(pagamento);

        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    public PagamentoDto atualizar(Long id, PagamentoDto pagamentoDto) {
//        var pagamentoBanco = repository.findById(id);
//
//        if(!pagamentoBanco.isPresent()) {
//            throw new EntityNotFoundException();
//        }
//
//        var pagamento = modelMapper.map(pagamentoDto, Pagamento.class);
//        pagamento.setId(id);
//        repository.save(pagamento);
//
//        return modelMapper.map(pagamento, PagamentoDto.class);

        Pagamento pagamento = modelMapper.map(pagamentoDto, Pagamento.class);
        pagamento.setId(id);
        pagamento = repository.save(pagamento);
        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }


//    public void confirmarPagamento(Long id) {
//        var pagamento = repository.findById(id);
//
//        if(!pagamento.isPresent()) {
//            throw new EntityNotFoundException();
//        }
//
//        pagamento.get().setStatus(Status.CONFIRMADO);
//        repository.save(pagamento.get());
//        pedido.atualizarPagamento(pagamento.get().getPedidoId());
//    }

    public void alteraStatus(Long id) {
        Optional<Pagamento> pagamento = repository.findById(id);

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO_SEM_INTEGRACAO);
        repository.save(pagamento.get());
    }
}
