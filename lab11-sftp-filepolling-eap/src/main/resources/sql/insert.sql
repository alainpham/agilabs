INSERT INTO commande
(
	commandeid, 
	statutcommande, 
	datelivraison, 
	fournisseur, 
	codearcticle, 
	nomarticle, 
	quantite, 
	prixunitaire, 
	remise, 
	prixtotal)
VALUES(
	:#commandeId, 
	:#statutCommande, 
	cast(:#dateLivraison as date), 
	:#fournisseur, 
	:#codeArcticle, 
	:#momArticle, 
	cast(:#quantite as integer), 
	cast(:#prixUnitaire as numeric), 
	cast(:#remise as integer), 
	cast(:#prixTotal as numeric));
