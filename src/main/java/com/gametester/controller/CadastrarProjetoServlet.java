package com.gametester.controller;

import com.gametester.dao.ProjetoDAO;
import com.gametester.dao.UsuarioDAO; // Para buscar usuários para a lista de membros
import com.gametester.model.Projeto;
import com.gametester.model.Usuario;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/cadastrarProjeto") // Caminho corrigido para /admin/*
public class CadastrarProjetoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProjetoDAO projetoDAO;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() {
        projetoDAO = new ProjetoDAO();
        usuarioDAO = new UsuarioDAO();
    }

    // Método GET para exibir o formulário de cadastro de projeto
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // A verificação de administrador aqui é uma camada extra,
        // o AuthorizationFilter deve ser o principal protetor.
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        if (usuarioLogado == null || !"ADMINISTRADOR".equals(usuarioLogado.getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=acessoNegado");
            return;
        }

        try {
            // Carregar usuários (ex: apenas Testadores ou todos) para popular a seleção de membros
            List<Usuario> listaUsuariosDisponiveis = usuarioDAO.listarTodosUsuarios(); // Ou filtrar por perfil se necessário
            request.setAttribute("listaUsuariosDisponiveis", listaUsuariosDisponiveis);
        } catch (Exception e) {
            // Tratar erro ao buscar usuários, talvez definir uma lista vazia e logar o erro
            request.setAttribute("listaUsuariosDisponiveis", new ArrayList<Usuario>());
            System.err.println("Erro ao carregar usuários para o formulário de projeto: " + e.getMessage());
            // Considerar mostrar uma mensagem de erro na página
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/cadastrar-projeto.jsp");
        dispatcher.forward(request, response);
    }

    // Método POST para processar o cadastro do projeto
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        // Verificação de autorização
        if (usuarioLogado == null || !"ADMINISTRADOR".equals(usuarioLogado.getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=acessoNaoAutorizado");
            return;
        }

        String nomeProjeto = request.getParameter("nomeProjeto");
        String descricao = request.getParameter("descricao");
        String[] membrosIdsStr = request.getParameterValues("membrosIds"); // IDs dos usuários selecionados como membros

        // Validação
        if (nomeProjeto == null || nomeProjeto.trim().isEmpty()) {
            request.setAttribute("mensagemErro", "O nome do projeto é obrigatório.");
            repopularFormularioEForward(request, response); // Repopula e encaminha de volta para o form
            return;
        }

        Projeto projeto = new Projeto();
        projeto.setNome(nomeProjeto.trim());
        projeto.setDescricao(descricao != null ? descricao.trim() : null);

        // Processar membros permitidos
        if (membrosIdsStr != null && membrosIdsStr.length > 0) {
            List<Usuario> membrosPermitidos = new ArrayList<>();
            for (String idStr : membrosIdsStr) {
                try {
                    int usuarioId = Integer.parseInt(idStr);
                    // É uma boa prática buscar o objeto Usuario para garantir que ele existe,
                    // mas o ProjetoDAO atual espera uma List<Usuario> e as insere na tabela de junção.
                    Usuario membro = usuarioDAO.buscarUsuarioPorId(usuarioId);
                    if (membro != null) {
                        membrosPermitidos.add(membro);
                    } else {
                        // Log: Usuário com ID X não encontrado para adicionar como membro
                        System.err.println("Tentativa de adicionar membro não existente ao projeto. ID: " + idStr);
                    }
                } catch (NumberFormatException e) {
                    // Log: ID de membro inválido
                    System.err.println("ID de membro inválido fornecido: " + idStr);
                }
            }
            projeto.setMembrosPermitidos(membrosPermitidos);
        }


        try {
            int idGerado = projetoDAO.inserirProjeto(projeto);

            if (idGerado != -1) {
                session.setAttribute("mensagemSucesso", "Projeto '" + projeto.getNome() + "' cadastrado com sucesso! ID: " + idGerado);
                response.sendRedirect(request.getContextPath() + "/admin/listarProjetos"); // Sugestão: redirecionar para uma lista de projetos
            } else {
                request.setAttribute("mensagemErro", "Falha ao cadastrar o projeto. Verifique os logs do servidor.");
                repopularFormularioEForward(request, response);
            }
        } catch (Exception e) { // Captura mais genérica para erros inesperados do DAO
            e.printStackTrace(); // Logar a exceção completa no servidor
            request.setAttribute("mensagemErro", "Erro inesperado ao salvar o projeto: " + e.getMessage());
            repopularFormularioEForward(request, response);
        }
    }

    private void repopularFormularioEForward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Salva os parâmetros atuais para repopular o formulário
        // É importante notar quegetParameterMap() retorna Map<String, String[]>, então o JSP precisará lidar com isso
        // ou você pode processar e setar atributos individuais.
        // Para simplicidade, vamos apenas repopular a lista de usuários disponíveis.
        try {
            List<Usuario> listaUsuariosDisponiveis = usuarioDAO.listarTodosUsuarios();
            request.setAttribute("listaUsuariosDisponiveis", listaUsuariosDisponiveis);
        } catch (Exception e) {
            request.setAttribute("listaUsuariosDisponiveis", new ArrayList<Usuario>());
            System.err.println("Erro ao carregar usuários para repopular formulário de projeto: " + e.getMessage());
        }

        // Para repopular os campos nomeProjeto e descricao, você pode setá-los como atributos
        // se não estiverem já disponíveis via request.getParameter() no JSP.
        // Ex: request.setAttribute("nomeProjetoValor", request.getParameter("nomeProjeto"));
        //     request.setAttribute("descricaoValor", request.getParameter("descricao"));
        // Os JSPs geralmente acessam `param.nomeCampo` para repopular, então isso pode não ser necessário
        // se o forward mantiver os parâmetros originais da requisição.

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/cadastrar-projeto.jsp");
        dispatcher.forward(request, response);
    }
}