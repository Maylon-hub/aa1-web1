package com.gametester.controller; // Ou seu pacote de filtros/controllers

import com.gametester.model.Usuario;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebFilter("/*")
public class AuthorizationFilter implements Filter {

    private static final Set<String> CAMINHOS_SEMPRE_PUBLICOS = new HashSet<>(Arrays.asList(
            "/login",          // LoginServlet POST
            "/login.jsp",      // Página de login GET
            "/index.jsp",      // Página inicial
            "/",               // Raiz da aplicação (com barra final)
            "/estrategias",
            ""                 // Raiz da aplicação (sem barra final - IMPORTANTE ADIÇÃO)
    ));

    private static final Set<String> CAMINHOS_PUBLICOS_VISITANTE = new HashSet<>(Arrays.asList(
            "/estrategias-publicas"
    ));

    private static final List<String> PADROES_RECURSOS_ESTATICOS = Arrays.asList(
            "/css/", "/js/", "/images/", "/fonts/"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AuthorizationFilter (com suporte a Visitante) inicializado!");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);

        String contextPath = request.getContextPath();
        String path = request.getRequestURI().substring(contextPath.length());
        String method = request.getMethod();

        System.out.println("--- AuthorizationFilter ---");
        System.out.println("Context Path: " + contextPath);
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Calculated Path: '" + path + "'");
        System.out.println("Method: " + method);
        System.out.println("Is in CAMINHOS_SEMPRE_PUBLICOS? " + CAMINHOS_SEMPRE_PUBLICOS.contains(path));
        System.out.println("Is Recurso Estatico? " + isRecursoEstatico(path));
        // --- Fim dos Logs ---

        if (CAMINHOS_SEMPRE_PUBLICOS.contains(path) || isRecursoEstatico(path) || "OPTIONS".equalsIgnoreCase(method)) {
            System.out.println("Filtro: Acesso público permitido para: " + path);
            chain.doFilter(request, response);
            return;
        }

        Usuario usuarioLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        if (CAMINHOS_PUBLICOS_VISITANTE.contains(path)) {
            System.out.println("Filtro: Acesso de visitante permitido para: " + path);
            chain.doFilter(request, response);
            return;
        }

        if (usuarioLogado == null) {
            System.out.println("Filtro: Usuário não logado tentando acessar caminho protegido '" + path + "'. Redirecionando para login.");
            response.sendRedirect(contextPath + "/login.jsp?error=naoAutenticado");
            return;
        }

        String tipoPerfil = usuarioLogado.getTipoPerfil();
        System.out.println("Filtro: Usuário logado ("+ tipoPerfil +") acessando: " + path);

        if (path.startsWith("/admin")) {
            if ("ADMINISTRADOR".equals(tipoPerfil)) {
                chain.doFilter(request, response);
            } else {
                System.out.println("Filtro: Usuário " + tipoPerfil + " tentando acessar " + path + " (ADMIN). Acesso negado.");
                response.sendRedirect(contextPath + "/login.jsp?error=acessoNegadoAdmin");
            }
            return;
        }

        if (path.startsWith("/testador")) {
            if ("TESTADOR".equals(tipoPerfil) || "ADMINISTRADOR".equals(tipoPerfil)) {
                chain.doFilter(request, response);
            } else {
                System.out.println("Filtro: Usuário " + tipoPerfil + " tentando acessar " + path + " (TESTADOR). Acesso negado.");
                response.sendRedirect(contextPath + "/login.jsp?error=acessoNegadoTestador");
            }
            return;
        }

        System.out.println("Filtro: Usuário logado " + tipoPerfil + " acessando caminho não explicitamente mapeado: " + path + ". Permitindo por padrão (verifique regras).");
        chain.doFilter(request, response);
    }

    private boolean isRecursoEstatico(String path) {
        for (String prefixo : PADROES_RECURSOS_ESTATICOS) {
            if (path.startsWith(prefixo)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
    }
}