package com.govmt.sgd.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.govmt.sgd.dto.request.UsuarioRequest;
import com.govmt.sgd.dto.response.UsuarioResponse;
import com.govmt.sgd.exception.InvalidArgumentException;
import com.govmt.sgd.exception.NotFoundException;
import com.govmt.sgd.mappers.UsuarioMapper;
import com.govmt.sgd.model.UserAuthenticated;
import com.govmt.sgd.model.Usuario;
import com.govmt.sgd.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService{

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse createUsuario(UsuarioRequest usuarioRequest){
        if (usuarioRepository.findByEmail(usuarioRequest.email()).isPresent()) {
            throw new InvalidArgumentException("Este e-mail já está cadastrado no sistema.");
        }
        
        Usuario usuario = usuarioMapper.toUsuarioFromRequest(usuarioRequest);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setPermissoes(List.of("DOCUMENTO:LER"));

        return usuarioMapper.toResponseFromUsuario(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> getAll(Pageable pageable){
        return usuarioRepository.findAll(pageable)
                .map(usuarioMapper::toResponseFromUsuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse findById(UUID id){
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toResponseFromUsuario)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    @Transactional
    public UsuarioResponse updateUsuario(UsuarioRequest usuarioRequest){
        if(!usuarioRequest.email().equals(getUsuarioLogado().getEmail()) && !getUsuarioLogado().getPermissoes().contains("*:*")){
            throw new InvalidArgumentException("Não é possível atualizar o outro usuário.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioRequest.id())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        usuarioMapper.updateUsuarioFromRequest(usuarioRequest, usuario);

        return usuarioMapper.toResponseFromUsuario(usuario);
    }

    @Transactional
    public UsuarioResponse updatePermissoes(UUID id, List<String> novasPermissoes) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        usuario.setPermissoes(novasPermissoes);

        return usuarioMapper.toResponseFromUsuario(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deleteUsuario(UUID id){
        Usuario usuarioExiste = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        usuarioExiste.setDeletadoEm(LocalDateTime.now()); //softdelete
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("Usuário não encontrado")); //mudar excecao para notauthenticated

        return new UserAuthenticated(usuario);
    }

    public Usuario getUsuarioLogado(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // O JWT username foi gerado com email no UserAuthenticated.java
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Utilizador autenticado não encontrado"));
    }
    
}
