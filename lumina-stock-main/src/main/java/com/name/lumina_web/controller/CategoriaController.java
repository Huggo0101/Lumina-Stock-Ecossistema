package com.name.lumina_web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.name.lumina_web.model.Categoria;
import com.name.lumina_web.repository.CategoriaRepository;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public String gerenciarCategorias(Model model) {
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("novaCategoria", new Categoria());
        return "categorias"; 
    }

    @PostMapping("/salvar")
    public String salvarCategoria(Categoria categoria, RedirectAttributes atributos) {
        categoriaRepository.save(categoria);
        atributos.addFlashAttribute("mensagem", "Categoria adicionada com sucesso!");
        return "redirect:/categorias";
    }

    @GetMapping("/deletar/{id}")
    public String deletarCategoria(@PathVariable Integer id, RedirectAttributes atributos) {
        try {
            categoriaRepository.deleteById(id);
            atributos.addFlashAttribute("mensagem", "Categoria excluída com sucesso!");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            atributos.addFlashAttribute("erro", "Ação bloqueada: Não é possível excluir uma categoria que já possui produtos cadastrados nela.");
        }
        return "redirect:/categorias";
    }
}