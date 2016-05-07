package br.com.yagotome.clicktrauma.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.yagotome.clicktrauma.modelo.Pergunta;
import br.com.yagotome.clicktrauma.modelo.Resposta;

public class PerguntaDaoImpl implements PerguntaDao {
	private EntityManager manager;
	
	public PerguntaDaoImpl(EntityManager manager) {
		this.manager = manager;
	}
	
	@Override
	public void insere(Pergunta pergunta) {
		manager.getTransaction().begin();
		manager.persist(pergunta);
		for (Resposta resposta : pergunta.getRespostas()) {
			manager.persist(resposta);
		}
		manager.getTransaction().commit();		
	}

	@Override
	public void remove(Pergunta pergunta) {
		manager.getTransaction().begin();
		pergunta = buscaPorId(pergunta.getId());
		for (Resposta resposta : pergunta.getRespostas()) {
			manager.remove(resposta);			
		}
		manager.remove(pergunta);
		manager.getTransaction().commit();
	}

	@Override
	public Pergunta buscaPorId(Long id) {
		return manager.find(Pergunta.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pergunta> lista() {
		return manager.createQuery("select p from Pergunta p").getResultList();
	}

	@Override
	public void atualiza(Pergunta pergunta) {
		manager.getTransaction().begin();
		manager.merge(pergunta);
		for (Resposta resposta : pergunta.getRespostas()) {
			manager.merge(resposta);
		}
		manager.getTransaction().commit();		
	}

}
