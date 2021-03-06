package br.com.yagotome.clicktrauma.dao;

import javax.persistence.EntityManager;

import br.com.yagotome.clicktrauma.modelo.usuario.Usuario;

public class UsuarioDaoImpl implements UsuarioDao {

	private EntityManager manager;
	
	public UsuarioDaoImpl(EntityManager manager) {
		this.manager = manager;
	}
	
	@Override
	public void insere(Usuario usuario) {
		manager.getTransaction().begin();
		manager.persist(usuario);
		manager.getTransaction().commit();
	}

	@Override
	public void atualiza(Usuario usuario) {
		manager.getTransaction().begin();
		manager.merge(usuario);
		manager.getTransaction().commit();		
	}

	@Override
	public Usuario buscaPorId(Long id) {			
		return manager.find(Usuario.class, id);
	}
	
}
