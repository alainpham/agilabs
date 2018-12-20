CREATE TABLE commande (
   commandeId VARCHAR(255),
   statutcommande VARCHAR(255),
   datelivraison TIMESTAMP,
   fournisseur VARCHAR(255),
   prixtotal numeric,
   codeArcticle VARCHAR(255),
   nomarticle VARCHAR(255),
   quantite integer,
   prixunitaire numeric,
   remise integer
   )