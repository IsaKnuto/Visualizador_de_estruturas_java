package Arvore_B;
   
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
 

class ImpressoraArvore {
    private static final int ESPACO_MINIMO = 2;
 
    private ImpressoraArvore() {
    }
 
    private static class Bloco {
        final List<String> linhas;
        final int largura;
        final int centro; 
 
        Bloco(List<String> linhas, int largura, int centro) {
            this.linhas = linhas;
            this.largura = largura;
            this.centro = centro;
        }
    }
 
    static String desenhar(No raiz) {
        if (raiz == null) {
            return "(árvore vazia)";
        }
        Bloco bloco = desenharNo(raiz);
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < bloco.linhas.size(); i++) {
            if (i > 0) {
                resultado.append('\n');
            }
            resultado.append(bloco.linhas.get(i).replaceAll("\\s+$", ""));
        }
        return resultado.toString();
    }
 
    private static Bloco desenharNo(No no) {
        String rotulo = montarRotulo(no);
        int larguraRotulo = rotulo.length();
 
        if (no.ehFolha()) {
            List<String> linhas = new ArrayList<>();
            linhas.add(rotulo);
            return new Bloco(linhas, larguraRotulo, larguraRotulo / 2);
        }
 
       
        int quantidadeFilhos = no.obterQuantidadeChaves() + 1;
        Bloco[] filhos = new Bloco[quantidadeFilhos];
        int somaLarguras = 0;
        int alturaFilhos = 0;
        for (int i = 0; i < quantidadeFilhos; i++) {
            filhos[i] = desenharNo(no.obterFilho(i));
            somaLarguras += filhos[i].largura;
            alturaFilhos = Math.max(alturaFilhos, filhos[i].linhas.size());
        }
 
       
        int espaco = ESPACO_MINIMO;
        int[] deslocamentos = new int[quantidadeFilhos];
        int[] centros = new int[quantidadeFilhos];
        int inicioRotulo = 0;
        while (true) {
            int posicao = 0;
            for (int i = 0; i < quantidadeFilhos; i++) {
                deslocamentos[i] = posicao;
                centros[i] = posicao + filhos[i].centro;
                posicao += filhos[i].largura + espaco;
            }
            int primeiroCentro = centros[0];
            int ultimoCentro = centros[quantidadeFilhos - 1];
            inicioRotulo = (primeiroCentro + ultimoCentro) / 2 - larguraRotulo / 2;
            if (primeiroCentro <= inicioRotulo - 2 && ultimoCentro >= inicioRotulo + larguraRotulo + 1) {
                break;
            }
            espaco++;
        }
 
        int largura = somaLarguras + espaco * (quantidadeFilhos - 1);
        int primeiro = centros[0];
        int ultimo = centros[quantidadeFilhos - 1];
        int fimRotulo = inicioRotulo + larguraRotulo; // posição logo após o rótulo
 
        
        char[] linhaRotulo = linhaEmBranco(largura);
        rotulo.getChars(0, larguraRotulo, linhaRotulo, inicioRotulo);
 
        
        char[] ligacao1 = linhaEmBranco(largura);
        Arrays.fill(ligacao1, primeiro + 1, ultimo, '_');
        Arrays.fill(ligacao1, inicioRotulo - 1, fimRotulo + 1, ' ');
        ligacao1[inicioRotulo - 1] = '/';
        ligacao1[fimRotulo] = '\\';
 
       
        char[] ligacao2 = linhaEmBranco(largura);
        ligacao2[primeiro] = '/';
        ligacao2[ultimo] = '\\';
        for (int i = 1; i < quantidadeFilhos - 1; i++) {
            ligacao2[centros[i]] = '|';
            if (centros[i] > inicioRotulo - 1 && centros[i] < fimRotulo) {
                ligacao1[centros[i]] = '|';
            }
        }
 
        
        List<String> linhas = new ArrayList<>();
        linhas.add(new String(linhaRotulo));
        linhas.add(new String(ligacao1));
        linhas.add(new String(ligacao2));
        for (int r = 0; r < alturaFilhos; r++) {
            char[] linha = linhaEmBranco(largura);
            for (int i = 0; i < quantidadeFilhos; i++) {
                if (r < filhos[i].linhas.size()) {
                    String texto = filhos[i].linhas.get(r);
                    texto.getChars(0, texto.length(), linha, deslocamentos[i]);
                }
            }
            linhas.add(new String(linha));
        }
        return new Bloco(linhas, largura, (primeiro + ultimo) / 2);
    }
 
    private static String montarRotulo(No no) {
        StringBuilder rotulo = new StringBuilder();
        for (int i = 0; i < no.obterQuantidadeChaves(); i++) {
            rotulo.append('[').append(no.obterChave(i)).append(']');
        }
        return rotulo.toString();
    }
 
    private static char[] linhaEmBranco(int largura) {
        char[] linha = new char[largura];
        Arrays.fill(linha, ' ');
        return linha;
    }
}
 

