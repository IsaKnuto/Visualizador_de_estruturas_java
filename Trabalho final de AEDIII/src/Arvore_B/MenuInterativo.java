package Arvore_B;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MenuInterativo {
    private final int grauMinimo;
    private ArvoreB arvore;
    private final Scanner leitor;

    public MenuInterativo(int grauMinimo) {
        this.grauMinimo = grauMinimo;
        this.arvore = new ArvoreB(grauMinimo);
        this.leitor = new Scanner(System.in);
    }

    public void iniciar() {
        System.out.println("=== Árvore B (grau mínimo t = " + grauMinimo + ") ===");
        mostrarArvore();

        boolean executando = true;
        while (executando) {
            exibirMenu();
            if (!leitor.hasNextLine()) {
                break;
            }
            String opcao = leitor.nextLine().trim();

            switch (opcao) {
                case "1":
                    inserirNumeros();
                    break;
                case "2":
                    removerNumeros();
                    break;
                case "3":
                    buscarNumeros();
                    break;
                case "4":
                    mostrarArvore();
                    break;
                case "5":
                    arvore = new ArvoreB(grauMinimo);
                    System.out.println("Árvore esvaziada.");
                    mostrarArvore();
                    break;
                case "0":
                    executando = false;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
        System.out.println("Encerrando. Até mais!");
    }

    // ---------------- Ações ----------------

    private void inserirNumeros() {
        List<Integer> numeros = lerNumeros("Digite os números a inserir (separados por espaço): ");
        for (int numero : numeros) {
            if (arvore.inserir(numero)) {
                System.out.println("\n>>> Inserido: " + numero);
            } else {
                System.out.println("\n>>> " + numero + " já existe na árvore (ignorado).");
            }
            mostrarArvore();
        }
    }

    private void removerNumeros() {
        List<Integer> numeros = lerNumeros("Digite os números a remover (separados por espaço): ");
        for (int numero : numeros) {
            if (arvore.remover(numero)) {
                System.out.println("\n>>> Removido: " + numero);
            } else {
                System.out.println("\n>>> " + numero + " não existe na árvore.");
            }
            mostrarArvore();
        }
    }

    private void buscarNumeros() {
        List<Integer> numeros = lerNumeros("Digite os números a buscar (separados por espaço): ");
        for (int numero : numeros) {
            System.out.println(numero + (arvore.buscar(numero) ? " -> encontrado" : " -> não encontrado"));
        }
    }

    // ---------------- Auxiliares ----------------

    private void mostrarArvore() {
        System.out.println("\n--- Estado atual da árvore ---");
        arvore.imprimir();
        System.out.println("------------------------------");
    }

    private void exibirMenu() {
        System.out.println("\n[1] Inserir  [2] Remover  [3] Buscar  [4] Mostrar  [5] Limpar  [0] Sair");
        System.out.print("Opção: ");
    }

    private List<Integer> lerNumeros(String mensagem) {
        System.out.print(mensagem);
        List<Integer> numeros = new ArrayList<>();
        if (!leitor.hasNextLine()) {
            return numeros;
        }
        String linha = leitor.nextLine().trim();
        if (linha.isEmpty()) {
            return numeros;
        }
        for (String parte : linha.split("[\\s,;]+")) {
            try {
                numeros.add(Integer.parseInt(parte));
            } catch (NumberFormatException e) {
                System.out.println("'" + parte + "' não é um número inteiro válido (ignorado).");
            }
        }
        return numeros;
    }
}