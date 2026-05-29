package com.name.lumina_web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.name.lumina_web.dto.ItemVendaDTO;
import com.name.lumina_web.model.ItemVenda;
import com.name.lumina_web.model.Produto;
import com.name.lumina_web.model.Venda;
import com.name.lumina_web.repository.CategoriaRepository;
import com.name.lumina_web.repository.ItemVendaRepository;
import com.name.lumina_web.repository.ProdutoRepository;
import com.name.lumina_web.repository.VendaRepository;

import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

@Controller
public class ProdutoController {

    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private VendaRepository vendaRepository;
    @Autowired private ItemVendaRepository itemVendaRepository;
    @Autowired private CategoriaRepository categoriaRepository;

    // --- FUNÇÃO AUXILIAR ---
    private void preencherQuantidadesVendidas(List<Produto> produtos) {
        for (Produto p : produtos) {
            int totalVendido = itemVendaRepository.sumQuantidadeByProdutoId(p.getId());
            p.setQuantidadeVendida(totalVendido);
        }
    }

    @GetMapping("/") 
    public String landingPage() { return "index"; }

    @GetMapping("/menu")
    public String menuPrincipal() { return "menu"; }

    @GetMapping("/dashboard")
    public String listarProdutos(Model model) {
        // Usa o novo método do repositório para mostrar apenas os produtos ativos
        List<Produto> produtos = produtoRepository.findByAtivoTrue();
        preencherQuantidadesVendidas(produtos);
        model.addAttribute("produtos", produtos);
        return "dashboard"; 
    }

    @GetMapping("/novo")
    public String abrirFormulario(Model model) {
        model.addAttribute("produto", new Produto());
        model.addAttribute("categorias", categoriaRepository.findAll()); 
        return "cadastro"; 
    }

    @PostMapping("/salvar")
    public String salvarProduto(Produto produto) {
        // Garante que o produto será salvo com o status ativo = true
        produto.setAtivo(true);
        produtoRepository.save(produto);
        return "redirect:/dashboard"; 
    }

    // --- ROTA DE EXCLUSÃO COM EXCLUSÃO LÓGICA ---
    @GetMapping("/deletar/{id}")
    public String deletarProduto(@PathVariable Long id, RedirectAttributes atributos) {
        
        // Verifica proativamente se o produto possui histórico de vendas
        int totalVendido = itemVendaRepository.sumQuantidadeByProdutoId(id);
        
        if (totalVendido > 0) {
            // Se já tem vendas, faz a exclusão lógica (oculta o produto)
            Produto p = produtoRepository.findById(id).orElseThrow();
            p.setAtivo(false);
            produtoRepository.save(p);
            atributos.addFlashAttribute("mensagem", "Produto arquivado! Ele foi removido da tela de vendas, mas o histórico de lucros foi mantido.");
        } else {
            // Se não tem vendas, exclui de verdade do banco
            produtoRepository.deleteById(id);
            atributos.addFlashAttribute("mensagem", "Produto excluído com sucesso!");
        }
        
        return "redirect:/dashboard";
    }

    @GetMapping("/editar/{id}")
    public String editarProduto(@PathVariable Long id, Model model) {
        Produto produtoExistente = produtoRepository.findById(id).orElseThrow();
        model.addAttribute("produto", produtoExistente);
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "cadastro"; 
    }
    
    // --- ROTAS DE VENDA ---
    @PostMapping("/vender")
    public String realizarVenda(@RequestParam Long id, @RequestParam int quantidade, RedirectAttributes atributos) {
        Produto produto = produtoRepository.findById(id).orElseThrow();
        
        if (produto.getQuantidadeEstoque() >= quantidade) {
            Venda novaVenda = new Venda();
            vendaRepository.save(novaVenda);

            ItemVenda item = new ItemVenda();
            item.setVenda(novaVenda);
            item.setProduto(produto);
            item.setQuantidade(quantidade);
            item.setPrecoUnitario(produto.getPrecoVenda());
            itemVendaRepository.save(item);

            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
            produtoRepository.save(produto);
            
            atributos.addFlashAttribute("mensagem", "Venda realizada!");
        } else {
            atributos.addFlashAttribute("erro", "Erro: Estoque insuficiente.");
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/finalizar-venda")
    @ResponseBody
    public String finalizarVendaCarrinho(@RequestBody List<ItemVendaDTO> itens) {
        for (ItemVendaDTO itemDTO : itens) {
            Produto p = produtoRepository.findById(itemDTO.getId()).orElse(null);
            if (p == null || p.getQuantidadeEstoque() < itemDTO.getQuantidade()) {
                return "Erro: Estoque insuficiente para o produto ID: " + itemDTO.getId();
            }
        }

        Venda novaVenda = new Venda();
        vendaRepository.save(novaVenda);

        for (ItemVendaDTO itemDTO : itens) {
            Produto p = produtoRepository.findById(itemDTO.getId()).get();
            
            ItemVenda item = new ItemVenda();
            item.setVenda(novaVenda);
            item.setProduto(p);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPrecoUnitario(p.getPrecoVenda());
            itemVendaRepository.save(item);

            p.setQuantidadeEstoque(p.getQuantidadeEstoque() - itemDTO.getQuantidade());
            produtoRepository.save(p);
        }
        
        return "Sucesso"; 
    }
}