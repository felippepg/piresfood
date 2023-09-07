package br.com.piresfood.pedidos.config;

import br.com.piresfood.pedidos.dto.PagamentoDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PagamentoListener {

    @RabbitListener(queues = "pagamentos.detalhe-pedido")
    public void receberMensagem(PagamentoDto pagamento) {
        String message = """
                Dados do pagto: %s
                Número do pedido: %s
                Valor: %s
                Status: %s
                """.formatted
                (pagamento.getId(),
                pagamento.getPedidoId(),
                pagamento.getValor(),
                pagamento.getStatus()
                );

        System.out.println(message);
    }
}
