README
======
1. Si è deciso di caricare l'intero file di input in un buffer (come buffer è stata usata una stringa
   in quanto in Java è possibile avere elementi della classe <code>java.lang.String</code> con dimensione massima
   di (2<sup>31</sup> - 1)).
   Per gestire la fine del file si è aggiunto il carattere speciale <code>\0</code> alla fine dell'input
   e si è usato un flag booleano (denominato "flag") per poter uscire dal ciclo <code>while</code> quando non ci sono ulteriori
   caratteri da leggere.
   Si parte ponendo il flag a true e si pone a false quando il carattere letto è uguale a <code>\0</code>.

2. I whitespace all'interno del file vengono consumati e di conseguenza ignorati. Per whitespace si intendono
   tutti quei caratteri non considerati esplicitamente. Esempi sono: <code>\n \r \t</code> ecc.

3. Per la tabella delle stringhe si è usata una classe <code>SymbolTable</code> in cui è presente una collezione
   (un <code>ArrayList</code>) di <code>SymbolTableRow</code> che rappresenta una riga della Tabella.
   Per ogni riga è specificato un ID numerico, il Lessema associato a quell'ID e il Token associato
   al lessema (per la sola comodità di gestire le keywords).

GESTIONE ERRORI
----------------
1. Gli 0 (zero) davanti ad un numero intero (es. 0052) o dopo la virgola (1.25000) sono stati gestiti
   in modo che l'analizzatore lessicale restituisca i seguenti token:
   <code><NUMBER, "0"><NUMBER, "0"><NUMBER, "52"> e <NUMBER, "1.25"><NUMBER, "0"><NUMBER, "0"><NUMBER, "0"></code>
   Per evitare un numero decimale errato (es. 50.00), abbiamo progettato l'analizzatore lessicale
   in modo che restituisca i seguenti token: <code><NUMBER, "50"><ERROR, "."><NUMBER, "0"><NUMBER, "0"></code>

2. Si è deciso di restituire un token <code><ERROR, "lex"</code>> dove <code>lex</code> è un lessema che non rispetta nessun pattern
   dei token validi.

**Nota:** Per un'errata configurazione di <code>git</code>, le credenziali degli autori non risultano essere quelle 
di GitLab e quindi risultano degli username diversi rispetto a quelli comunicati.
Per evitare di avere conflitti nei vari commit abbiamo deciso di non modificarli. <br/>

| Username Errato     | Username Corretto | Studente |
| -----------: | :----------- | :----- |
| gnoanto94      | gnoanto       | Antonucci G. |
| Sfoffo   | alessioromanogitlab | Romano A.|