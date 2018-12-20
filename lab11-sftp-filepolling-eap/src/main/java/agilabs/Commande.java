package agilabs;

import java.util.Date;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord( separator = "\\|" ,crlf="UNIX",skipFirstLine = true)
public class Commande {

	@DataField(pos=1)
	private String commandeId;
	
	@DataField(pos=2)
	private String statutCommande;
	@DataField(pos=3,pattern="dd/MM/yyyy")
	private Date dateLivraison;
	@DataField(pos=4)
	private String  fournisseur;
	@DataField(pos=5)
	private String codeArcticle;
	@DataField(pos=6)
	private String momArticle;
	@DataField(pos=7)
	private Integer quantite;
	@DataField(pos=8)
	private Float prixUnitaire;
	@DataField(pos=9)
	private Integer remise;
	@DataField(pos=10)
	private Integer numeroSequence;
	
	private Float prixTotal;
	
	
	public Float getPrixTotal() {
		return prixTotal;
	}
	public void setPrixTotal(Float prixTotal) {
		this.prixTotal = prixTotal;
	}
	public String getCommandeId() {
		return commandeId;
	}
	public void setCommandeId(String commandeId) {
		this.commandeId = commandeId;
	}
	public String getStatutCommande() {
		return statutCommande;
	}
	public void setStatutCommande(String statutCommande) {
		this.statutCommande = statutCommande;
	}
	public Date getDateLivraison() {
		return dateLivraison;
	}
	public void setDateLivraison(Date dateLivraison) {
		this.dateLivraison = dateLivraison;
	}
	public String getFournisseur() {
		return fournisseur;
	}
	public void setFournisseur(String fournisseur) {
		this.fournisseur = fournisseur;
	}
	public String getCodeArcticle() {
		return codeArcticle;
	}
	public void setCodeArcticle(String codeArcticle) {
		this.codeArcticle = codeArcticle;
	}
	public String getMomArticle() {
		return momArticle;
	}
	public void setMomArticle(String momArticle) {
		this.momArticle = momArticle;
	}
	public Integer getQuantite() {
		return quantite;
	}
	public void setQuantite(Integer quantite) {
		this.quantite = quantite;
	}
	public Float getPrixUnitaire() {
		return prixUnitaire;
	}
	public void setPrixUnitaire(Float prixUnitaire) {
		this.prixUnitaire = prixUnitaire;
	}
	public Integer getRemise() {
		return remise;
	}
	public void setRemise(Integer remise) {
		this.remise = remise;
	}
	public Integer getNumeroSequence() {
		return numeroSequence;
	}
	public void setNumeroSequence(Integer numeroSequence) {
		this.numeroSequence = numeroSequence;
	}
	
	
	
}
