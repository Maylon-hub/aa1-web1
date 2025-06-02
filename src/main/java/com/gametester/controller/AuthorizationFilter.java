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

    // Caminhos que são SEMPRE públicos (não precisam de login nem de perfil específico)
    // Inclui login, recursos estáticos, e a página inicial.
    private static final Set<String> CAMINHOS_SEMPRE_PUBLICOS = new HashSet<>(Arrays.asList(
            "/login",          // LoginServlet POST
            "/login.jsp",      // Página de login GET
            "/index.jsp",      // Página inicial
            "/"                // Raiz da aplicação (geralmente leva ao index.jsp)
    ));

    // Caminhos de funcionalidades acessíveis por VISITANTES (e, por hierarquia, por usuários logados)
    private static final Set<String> CAMINHOS_PUBLICOS_VISITANTE = new HashSet<>(Arrays.asList(
            "/publico/listaEstrategias.jsp",
            "/estrategias-publicas",
            "/estrategias"
            // Exemplo para R6 - Listar Estratégias publicamente
            // Adicionar outras rotas públicas de funcionalidades aqui
    ));

    // Padrões de início de caminho para recursos estáticos
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

        // 1. Permitir acesso a caminhos SEMPRE públicos e recursos estáticos
        if (CAMINHOS_SEMPRE_PUBLICOS.contains(path) || isRecursoEstatico(path) || "OPTIONS".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        Usuario usuarioLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        // 2. Permitir acesso a caminhos de funcionalidades públicas para VISITANTES (e usuários logados)
        if (CAMINHOS_PUBLICOS_VISITANTE.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 3. Se chegou aqui, o caminho é PROTEGIDO e requer login
        if (usuarioLogado == null) {
            System.out.println("Filtro: Usuário não logado tentando acessar caminho protegido " + path + ". Redirecionando para login.");
            response.sendRedirect(contextPath + "/login.jsp?error=naoAutenticado");
            return;
        }

        // 4. Usuário está logado, verificar autorização específica do perfil
        String tipoPerfil = usuarioLogado.getTipoPerfil();

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
            if ("TESTADOR".equals(tipoPerfil) || "ADMINISTRADOR".equals(tipoPerfil)) { // Admin também acessa
                chain.doFilter(request, response);
            } else {
                System.out.println("Filtro: Usuário " + tipoPerfil + " tentando acessar " + path + " (TESTADOR). Acesso negado.");
                response.sendRedirect(contextPath + "/login.jsp?error=acessoNegadoTestador");
            }
            return;
        }

        // Se o usuário está logado mas o caminho não corresponde a /admin ou /testador
        // e não é público, pode ser uma área não mapeada ainda.
        // Por segurança, poderíamos redirecionar para o dashboard ou uma página de erro.
        // Ou, se todas as rotas logadas começam com /admin ou /testador, esta parte pode não ser alcançada.
        System.out.println("Filtro: Usuário logado " + tipoPerfil + " acessando caminho não explicitamente mapeado: " + path + ". Verifique as regras do filtro.");
        // Por enquanto, se chegou aqui logado e não foi bloqueado, permite.
        // Considere adicionar uma regra mais estrita se necessário.
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
        // Limpeza de recursos, se necessário
    }
}