# Hostaly

 ## Regole di business implementate:
 - **R1** — una prenotazione richiede almeno una notte e check-in non nel passato;
- **R2** — la camera deve essere libera per tutto l'intervallo (nessuna sovrapposizione);
- **R3** — lo stato PENDING/CONFIRMED/CHECKED_IN occupa la camera;
 - **R4** — la richiesta del cliente nasce PENDING e va confermata dallo staff, l'hall-walk-in nasce direttamente CONFIRMED;
- **R5** — cancellazione gratuita se avvenuta con almeno 24h di anticipo sul check-in, altrimenti penale pari alla prima notte; le richieste PENDING si annullano gratis;
- **R6** — il check-in e' consentito dal giorno di check-in e solo su camera pronta (non DA PULIRE, non in MANUTENZIONE);
- **R7** — il check-out emette la fattura e mette la camera in DA PULIRE;
 - **R8** — i servizi aggiuntivi si aggiungono solo a prenotazioni attive, con prezzo "snapshot" preso dal catalogo e data all'interno del soggiorno.
 - Manutenzione delle camera: operazione riservata all'**Admin**.
