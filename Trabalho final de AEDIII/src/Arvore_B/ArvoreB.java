package Arvore_B;


public class ArvoreB {
    
    private static final boolean USAR_SUCESSOR_NA_REMOCAO = true;
 
    private final int grauMinimo;
    private final int maximoChaves;
    private final int minimoChaves;
    private No raiz;
 
    public ArvoreB() {
        this(2);
    }
 
    public ArvoreB(int grauMinimo) {
        if (grauMinimo < 2) {
            throw new IllegalArgumentException("O grau mínimo deve ser >= 2");
        }
        this.grauMinimo = grauMinimo;
        this.maximoChaves = 2 * grauMinimo - 1;
        this.minimoChaves = grauMinimo - 1;
        this.raiz = null;
    }
 
    // ===================== BUSCA =====================
 
    public boolean buscar(int chave) {
        return buscarNoNo(raiz, chave);
    }
 
    private boolean buscarNoNo(No no, int chave) {
        if (no == null) {
            return false;
        }
        int indice = no.indicePrimeiraChaveMaiorOuIgual(chave);
        if (indice < no.obterQuantidadeChaves() && no.obterChave(indice) == chave) {
            return true;
        }
        if (no.ehFolha()) {
            return false;
        }
        return buscarNoNo(no.obterFilho(indice), chave);
    }
 
    // ===================== INSERÇÃO =====================
 
   
    public boolean inserir(int chave) {
        if (buscar(chave)) {
            return false;
        }
        if (raiz == null) {
            raiz = new No(grauMinimo, true);
        }
        inserirNoNo(raiz, chave);
 
        // Se a raiz transbordou, ela quebra e a chave do meio vira a nova raiz.
        if (raiz.obterQuantidadeChaves() > maximoChaves) {
            No novaRaiz = new No(grauMinimo, false);
            novaRaiz.definirFilho(0, raiz);
            quebrarFilho(novaRaiz, 0);
            raiz = novaRaiz;
        }
        return true;
    }
 
    private void inserirNoNo(No no, int chave) {
        if (no.ehFolha()) {
            int i = no.obterQuantidadeChaves() - 1;
            while (i >= 0 && chave < no.obterChave(i)) {
                no.definirChave(i + 1, no.obterChave(i));
                i--;
            }
            no.definirChave(i + 1, chave);
            no.definirQuantidadeChaves(no.obterQuantidadeChaves() + 1);
            return;
        }
 
        int indice = no.indicePrimeiraChaveMaiorOuIgual(chave);
        inserirNoNo(no.obterFilho(indice), chave);
 
        // Na volta da recursão: se o filho transbordou, quebra.
        if (no.obterFilho(indice).obterQuantidadeChaves() > maximoChaves) {
            quebrarFilho(no, indice);
        }
    }
 
    
    private void quebrarFilho(No pai, int indice) {
        No esquerdo = pai.obterFilho(indice);
        int meio = grauMinimo - 1; // com número par de chaves, usa o meio da esquerda
        int chaveDoMeio = esquerdo.obterChave(meio);
        int quantidadeDireita = esquerdo.obterQuantidadeChaves() - meio - 1;
 
        No direito = new No(grauMinimo, esquerdo.ehFolha());
        for (int j = 0; j < quantidadeDireita; j++) {
            direito.definirChave(j, esquerdo.obterChave(meio + 1 + j));
        }
        if (!esquerdo.ehFolha()) {
            for (int j = 0; j <= quantidadeDireita; j++) {
                direito.definirFilho(j, esquerdo.obterFilho(meio + 1 + j));
                esquerdo.definirFilho(meio + 1 + j, null);
            }
        }
        direito.definirQuantidadeChaves(quantidadeDireita);
        esquerdo.definirQuantidadeChaves(meio);
 
        
        for (int j = pai.obterQuantidadeChaves(); j >= indice + 1; j--) {
            pai.definirFilho(j + 1, pai.obterFilho(j));
        }
        pai.definirFilho(indice + 1, direito);
 
        for (int j = pai.obterQuantidadeChaves() - 1; j >= indice; j--) {
            pai.definirChave(j + 1, pai.obterChave(j));
        }
        pai.definirChave(indice, chaveDoMeio);
        pai.definirQuantidadeChaves(pai.obterQuantidadeChaves() + 1);
    }
 
    // ===================== REMOÇÃO =====================
 
  
    public boolean remover(int chave) {
        if (raiz == null || !buscar(chave)) {
            return false;
        }
        removerDoNo(raiz, chave);
 
        if (raiz.obterQuantidadeChaves() == 0) {
            raiz = raiz.ehFolha() ? null : raiz.obterFilho(0);
        }
        return true;
    }
 
    private void removerDoNo(No no, int chave) {
        int indice = no.indicePrimeiraChaveMaiorOuIgual(chave);
        boolean achouNesteNo = indice < no.obterQuantidadeChaves()
                && no.obterChave(indice) == chave;
 
        if (achouNesteNo) {
            if (no.ehFolha()) {
                removerChaveDeFolha(no, indice);
            } else if (USAR_SUCESSOR_NA_REMOCAO) {
                int sucessor = obterSucessor(no, indice);   // menor dos maiores
                no.definirChave(indice, sucessor);
                removerDoNo(no.obterFilho(indice + 1), sucessor);
                reequilibrarFilho(no, indice + 1);
            } else {
                int predecessor = obterPredecessor(no, indice); // maior dos menores
                no.definirChave(indice, predecessor);
                removerDoNo(no.obterFilho(indice), predecessor);
                reequilibrarFilho(no, indice);
            }
        } else {
            removerDoNo(no.obterFilho(indice), chave);
            reequilibrarFilho(no, indice);
        }
    }
 
    private void removerChaveDeFolha(No folha, int indice) {
        for (int i = indice + 1; i < folha.obterQuantidadeChaves(); i++) {
            folha.definirChave(i - 1, folha.obterChave(i));
        }
        folha.definirQuantidadeChaves(folha.obterQuantidadeChaves() - 1);
    }
 
    private int obterSucessor(No no, int indice) {
        No atual = no.obterFilho(indice + 1);
        while (!atual.ehFolha()) {
            atual = atual.obterFilho(0);
        }
        return atual.obterChave(0);
    }
 
    private int obterPredecessor(No no, int indice) {
        No atual = no.obterFilho(indice);
        while (!atual.ehFolha()) {
            atual = atual.obterFilho(atual.obterQuantidadeChaves());
        }
        return atual.obterChave(atual.obterQuantidadeChaves() - 1);
    }
 
   
    private void reequilibrarFilho(No pai, int indice) {
        if (pai.obterFilho(indice).obterQuantidadeChaves() >= minimoChaves) {
            return;
        }
        boolean temIrmaoEsquerdo = indice > 0;
        boolean temIrmaoDireito = indice < pai.obterQuantidadeChaves();
 
        if (temIrmaoEsquerdo && pai.obterFilho(indice - 1).obterQuantidadeChaves() > minimoChaves) {
            emprestarDoIrmaoEsquerdo(pai, indice);
        } else if (temIrmaoDireito && pai.obterFilho(indice + 1).obterQuantidadeChaves() > minimoChaves) {
            emprestarDoIrmaoDireito(pai, indice);
        } else if (temIrmaoEsquerdo) {
            fundirFilhos(pai, indice - 1);
        } else {
            fundirFilhos(pai, indice);
        }
    }
 
    private void emprestarDoIrmaoEsquerdo(No pai, int indice) {
        No filho = pai.obterFilho(indice);
        No irmao = pai.obterFilho(indice - 1);
 
        for (int i = filho.obterQuantidadeChaves() - 1; i >= 0; i--) {
            filho.definirChave(i + 1, filho.obterChave(i));
        }
        if (!filho.ehFolha()) {
            for (int i = filho.obterQuantidadeChaves(); i >= 0; i--) {
                filho.definirFilho(i + 1, filho.obterFilho(i));
            }
            filho.definirFilho(0, irmao.obterFilho(irmao.obterQuantidadeChaves()));
            irmao.definirFilho(irmao.obterQuantidadeChaves(), null);
        }
 
        filho.definirChave(0, pai.obterChave(indice - 1));
        pai.definirChave(indice - 1, irmao.obterChave(irmao.obterQuantidadeChaves() - 1));
 
        filho.definirQuantidadeChaves(filho.obterQuantidadeChaves() + 1);
        irmao.definirQuantidadeChaves(irmao.obterQuantidadeChaves() - 1);
    }
 
    private void emprestarDoIrmaoDireito(No pai, int indice) {
        No filho = pai.obterFilho(indice);
        No irmao = pai.obterFilho(indice + 1);
 
        filho.definirChave(filho.obterQuantidadeChaves(), pai.obterChave(indice));
        if (!filho.ehFolha()) {
            filho.definirFilho(filho.obterQuantidadeChaves() + 1, irmao.obterFilho(0));
        }
        pai.definirChave(indice, irmao.obterChave(0));
 
        for (int i = 1; i < irmao.obterQuantidadeChaves(); i++) {
            irmao.definirChave(i - 1, irmao.obterChave(i));
        }
        if (!irmao.ehFolha()) {
            for (int i = 1; i <= irmao.obterQuantidadeChaves(); i++) {
                irmao.definirFilho(i - 1, irmao.obterFilho(i));
            }
            irmao.definirFilho(irmao.obterQuantidadeChaves(), null);
        }
 
        filho.definirQuantidadeChaves(filho.obterQuantidadeChaves() + 1);
        irmao.definirQuantidadeChaves(irmao.obterQuantidadeChaves() - 1);
    }
 
  
    private void fundirFilhos(No pai, int indice) {
        No filho = pai.obterFilho(indice);
        No irmao = pai.obterFilho(indice + 1);
        int posicao = filho.obterQuantidadeChaves();
 
        filho.definirChave(posicao, pai.obterChave(indice));
        for (int i = 0; i < irmao.obterQuantidadeChaves(); i++) {
            filho.definirChave(posicao + 1 + i, irmao.obterChave(i));
        }
        if (!filho.ehFolha()) {
            for (int i = 0; i <= irmao.obterQuantidadeChaves(); i++) {
                filho.definirFilho(posicao + 1 + i, irmao.obterFilho(i));
            }
        }
        filho.definirQuantidadeChaves(posicao + 1 + irmao.obterQuantidadeChaves());
 
        for (int i = indice + 1; i < pai.obterQuantidadeChaves(); i++) {
            pai.definirChave(i - 1, pai.obterChave(i));
        }
        for (int i = indice + 2; i <= pai.obterQuantidadeChaves(); i++) {
            pai.definirFilho(i - 1, pai.obterFilho(i));
        }
        pai.definirFilho(pai.obterQuantidadeChaves(), null);
        pai.definirQuantidadeChaves(pai.obterQuantidadeChaves() - 1);
    }
 
    // ===================== IMPRESSÃO =====================
 
    public boolean estaVazia() {
        return raiz == null;
    }
 
    public void imprimir() {
        System.out.println(ImpressoraArvore.desenhar(raiz));
    }
}