package br.com.yagotome.clicktrauma.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.List;
import java.util.Stack;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.com.yagotome.clicktrauma.dao.PerguntaDao;
import br.com.yagotome.clicktrauma.dao.PerguntaDaoImpl;
import br.com.yagotome.clicktrauma.modelo.Pergunta;
import br.com.yagotome.clicktrauma.modelo.Resposta;

@Controller
public class PerguntaController {
	private PerguntaDao dao;
	
	public PerguntaController() {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("ClickTrauma");
		EntityManager manager = factory.createEntityManager();
		dao = new PerguntaDaoImpl(manager);
	}
	@RequestMapping("/pergunta/cadastro")
	public String cadastraPergunta() {
		return "pergunta/cadastro";
	}
	@RequestMapping(value = "/pergunta/insere", method = RequestMethod.POST)
	public String inserePerguta(Pergunta pergunta,
								@RequestParam("img-pergunta") MultipartFile imgPergunta,
								@RequestParam("img-resp-0") MultipartFile imgResp0,
								@RequestParam("img-resp-1") MultipartFile imgResp1,
								@RequestParam("img-resp-2") MultipartFile imgResp2,
								@RequestParam("img-resp-3") MultipartFile imgResp3,
								@RequestParam("img-resp-4") MultipartFile imgResp4,
								Model model,
								ServletRequest request) {
		int qtdResp = 5;
		if (pergunta.getRespostas().get(4).getTexto().isEmpty()) {
			pergunta.getRespostas().remove(4);
			qtdResp = 4;
		}
		for (Resposta resposta : pergunta.getRespostas()) {
			resposta.setPergunta(pergunta);
		}
		dao.insere(pergunta);
		try {
			String path = request.getServletContext().getRealPath("/") 
					+ request.getServletContext().getInitParameter("ImagesPath");		
			
			if (imgPergunta != null && !imgPergunta.isEmpty()) {
				String extension = extensao(imgPergunta.getOriginalFilename());
				String nome = "perguntas/" + pergunta.getId() + extension;
				salvaArq(imgPergunta, path + nome);
			}
			
			MultipartFile[] resps = new MultipartFile[]{imgResp0, imgResp1, imgResp2, imgResp3, imgResp4};
			for (int i = 0; i < qtdResp; i++) {
				if (resps[i] != null && !resps[i].isEmpty()) {
					String extension = extensao(resps[i].getOriginalFilename());
					String nome = "respostas/" + pergunta.getRespostas().get(i).getId() + extension;
					salvaArq(resps[i], path + nome);
				}
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		model.addAttribute("inserido", true);
		return "redirect:cadastro";
	}
	
	private String extensao(String nomeArq) {
		String aux = nomeArq;
		System.out.println(aux);
		String revExtension = "";
		for (int i = aux.length()-1; i >= 0 && aux.charAt(i) != '.'; i--) {
			revExtension += aux.charAt(i);
		}
		String extension = "";
		for (int i = revExtension.length()-1; i >= 0; i--) {
			extension += revExtension.charAt(i);
		}
		return "." + extension;
	}
	
	private void salvaArq(MultipartFile arq, String nome) throws Exception {
		File arquivo = new File(nome);
		System.out.println(arquivo.getAbsolutePath());
		BufferedOutputStream stream = new BufferedOutputStream(
				new FileOutputStream(arquivo));
        FileCopyUtils.copy(arq.getInputStream(), stream);
		stream.close();	
	}
	
	@RequestMapping("/pergunta/lista")
	public String lista(Model model) {
		List<Pergunta> perguntas = dao.lista();
		model.addAttribute("perguntas", perguntas);
		return "pergunta/lista";
	}
	
	@RequestMapping("/pergunta/remove")
	public void remove(Long id, HttpServletResponse response) {
		Pergunta p = new Pergunta();
		p.setId(id);
		dao.remove(p);
		response.setStatus(200);
	}
	@RequestMapping(method = RequestMethod.GET, value = "/pergunta/insereFile")
	public String abreFileUpload() {
		return "pergunta/fileUpload";
	}
	@RequestMapping(value = "/pergunta/insereFile", method = RequestMethod.POST)
	public String handleFileUpload(@RequestParam("name") String name,
								   @RequestParam("file") MultipartFile file,
								   ServletRequest request/*,
								   RedirectAttributes redirectAttributes*/) {
		if (name.contains("/")) {
			//redirectAttributes.addFlashAttribute("message", "Folder separators not allowed");
			return "redirect:/pergunta/insereFile";
		}
		
		if (name.contains("/")) {
			//redirectAttributes.addFlashAttribute("message", "Relative pathnames not allowed");
			return "redirect:/pergunta/insereFile";
		}
		if (!file.isEmpty()) {
			try {
				String path = request.getServletContext().getRealPath("/") 
						+ request.getServletContext().getInitParameter("ImagesPath");
				File arquivo = new File(path + name);
				System.out.println(arquivo.getAbsolutePath());
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(arquivo));
                FileCopyUtils.copy(file.getInputStream(), stream);
				stream.close();	
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return "redirect:/pergunta/insereFile";
	}
}
