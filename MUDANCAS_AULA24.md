# Log de mudanças — Aula 24 (quinto aplicativo)

Este documento lista tudo o que foi alterado no Pokédex para atender ao PDF
*"24 - Criação do quinto aplicativo"*. Cada item diz **qual requisito do PDF**
ele atende, **o que mudou** e **por quê**.

---

## Resumo: requisito do PDF → onde foi feito

| Requisito do PDF | Status | Onde |
|---|---|---|
| Menu com **NavigationUI**, de preferência na parte de baixo | ✅ | `activity_main.xml`, `menu/bottom_nav_menu.xml`, `MainActivity.kt` |
| **Usar grafo de navegação** | ✅ | `navigation/nav_graph.xml` + todos os fragments |
| **Retorno** da tela de resultado usando o grafo | ✅ | `PokemonDetailFragment.kt` (`navigateUp()`) |
| **Snackbar** ao favoritar/desfavoritar, com feedback | ✅ | `PokemonDetailFragment.kt` (com botão **Desfazer**) |
| **AlertDialog** confirmando a remoção na lista de favoritos | ✅ | `FavoritesFragment.kt` (Sim / Não / Ver detalhes) |
| **Tela de login** (login, senha, esqueci a senha, cadastre-se) | ✅ | `LoginFragment.kt`, `fragment_login.xml` |
| **Tela de cadastro** (nome, nick, e-mail, senha 1, senha 2) | ✅ | `RegisterFragment.kt`, `fragment_register.xml` |
| **Favoritos salvos no Firebase, separados por usuário** | ✅ código pronto, ⚠️ falta o seu `google-services.json` | `Data/remote/*` |
| Usar **MVVM** | ✅ cada tela nova tem o seu ViewModel | `ViewModel/*` |
| Usar **Fragments** | ✅ | `Ui/*Fragment.kt` |
| Usar `strings.xml` e `colors.xml` | ✅ todos os textos novos estão no `strings.xml` | `values/strings.xml` |

---

## 1. Dependências (Gradle)

### `gradle/libs.versions.toml`
- **Adicionado:** `navigation = "2.10.2"`, `firebaseBom = "33.10.0"` e `googleServices = "4.4.2"`.
- **Adicionadas as bibliotecas:** `androidx-navigation-fragment-ktx`, `androidx-navigation-ui-ktx`,
  `firebase-bom`, `firebase-auth` e `firebase-firestore`.
- **Adicionado o plugin:** `google-services` (lê o `google-services.json`).
- **Removidos:** `room` e `ksp` (bibliotecas e plugin). Os favoritos não ficam mais no Room.
- **Corrigido:** havia entradas duplicadas do Navigation (`navigationFragmentKtx` / `navigationUiKtx`)
  apontando para a mesma biblioteca. Ficou uma única versão.

### `build.gradle.kts` (raiz)
- O plugin `ksp` foi trocado por `google-services` (`apply false`).

### `app/build.gradle.kts`
- O plugin `ksp` foi trocado por `google-services`.
- **Removidas** duas linhas quebradas que impediam o build:
  `implementation("libs.androidx.navigation.fragment.ktx:2.5.3")` e a equivalente de `ui`.
  Por estarem entre aspas, eram lidas como uma coordenada Maven que não existe.
- **Removidas** as linhas duplicadas do Navigation e as do Room (`room-runtime` e `ksp(room-compiler)`).
- **Adicionados:** Navigation (fragment + ui), `platform(firebase-bom)`, `firebase-auth` e `firebase-firestore`.
- A linha `fragment-ktx:1.5.4` já tinha sido removida antes. O Navigation traz uma versão mais nova do Fragment.

---

## 2. Grafo de navegação

### `res/navigation/nav_graph.xml` — reescrito

O arquivo que já existia tinha três problemas:
1. `startDestination` apontava para o **detalhe**, que não funciona sem um Pokémon.
2. Faltava o **argumento** `pokemon` e a seta **Favoritos → Detalhe**.
3. Havia setas aba → aba (busca → grid, grid → favoritos…). Elas não são necessárias, porque quem troca de aba é a barra inferior.

Estrutura nova:

```
                     action_global_loginFragment (logout)
                                 │
   ┌────────────┐  cadastre-se  ┌─────────────┐
   │   login    │ ────────────▶ │  cadastro   │
   └─────┬──────┘ ◀── voltar ── └──────┬──────┘
         │ entrar (popUpTo login)      │ cadastrar (popUpTo login)
         ▼                             ▼
   ┌───────────────── abas da barra inferior ─────────────────┐
   │  searchFragment (início)   favoritesFragment   gridFragment │
   └──────────┬───────────────────────┬──────────────────┬─────┘
              └───────────────────────┼──────────────────┘
                                      ▼
                           pokemonDetailFragment
                           (argumento: pokemon)
```

- **`searchFragment` é o `startDestination`.** Este é o padrão recomendado de "navegação condicional".
  A tela principal é o início, e o app *redireciona* para o login quando não há usuário.
  Assim o NavigationUI sempre encontra a aba inicial na pilha, e trocar de aba funciona certo.
- **`loginFragment` → `searchFragment`** e **`registerFragment` → `searchFragment`** usam
  `popUpTo="@id/loginFragment" popUpToInclusive="true"`. Depois de entrar, o botão voltar
  **não** retorna ao login. Ele fecha o app.
- **`action_global_loginFragment`** é uma ação global com `popUpTo="@id/nav_graph"` inclusivo.
  Ela limpa toda a pilha e serve para o redirecionamento inicial e para o logout.
- **`pokemonDetailFragment`** declara `<argument android:name="pokemon" app:argType="…PokemonEntity"/>`.
  O `PokemonEntity` já era `Serializable`, então o grafo aceita passá-lo direto.
- As setas `search → detalhe` e `grid → detalhe` mantêm os **mesmos ids** do seu grafo.
  A nova é `action_favoritesFragment_to_pokemonDetailFragment`.

---

## 3. Barra de navegação inferior (NavigationUI)

### `res/menu/bottom_nav_menu.xml` — novo
- Três itens com os ícones que já existiam: busca, favoritos e grid, na mesma ordem da barra antiga.
- **O `id` de cada item é igual ao `id` do destino no grafo** (`searchFragment`,
  `favoritesFragment`, `gridFragment`). É isso que permite ao `setupWithNavController`
  saber qual tela abrir em cada clique.

### `res/color/bottom_nav_item_color.xml` — novo
- Um selector: aba selecionada em **amarelo**, as demais em **preto**.
- Ele substitui os antigos `paintSearch()`, `paintGrid()` e `paintFavorite()`.

### `res/layout/activity_main.xml` — alterado
- **Removidas** as três `ImageView` do topo (`search_image`, `fav_image`, `grid_image`).
- O `FragmentContainerView` virou o **host do grafo**:
  `android:name="androidx.navigation.fragment.NavHostFragment"`, `app:navGraph="@navigation/nav_graph"`
  e `app:defaultNavHost="true"`. O último faz o botão voltar do sistema navegar dentro do grafo.
- **Adicionada** a `BottomNavigationView` (`@id/bottom_nav`) presa ao fundo:
  fundo vermelho, ícones de 32dp, sem rótulos (`unlabeled`) e sem a "pílula" do Material 3,
  para manter o visual da barra antiga.
- A faixa vermelha da status bar (`status_bar_background`) continua igual.

### `Ui/MainActivity.kt` — reescrito
- **Removidos:** `View.OnClickListener`, `onClick()`, os três `paint*()` e as transações
  manuais com `supportFragmentManager.commit { replace(...) }`.
- **Adicionado:**
  - O `NavController` obtido do `NavHostFragment`.
  - `binding.bottomNav.setupWithNavController(navController)`, a ligação do **NavigationUI**:
    trocar de aba e marcar o ícone selecionado ficam automáticos.
  - `setOnItemReselectedListener`: tocar de novo na aba atual volta para a tela raiz dela.
    Por exemplo, você está no detalhe aberto pela Busca e toca em Busca.
  - Um `addOnDestinationChangedListener` que **esconde a barra** no login, no cadastro e no detalhe.
  - Se **não houver usuário logado** quando o app abre, a Activity navega para o login pela ação global.
- **Insets (edge-to-edge):**
  - A `BottomNavigationView` já reserva sozinha o espaço da barra de gestos.
    Por isso o padding inferior da tela agora é 0 quando a barra aparece. Antes o espaço ficaria em dobro.
  - Quando a barra está escondida, a tela recebe o padding da barra do sistema.
  - Quando o **teclado** está aberto, a tela recebe a altura do teclado e a barra inferior some.
    Assim os campos de login e cadastro não ficam escondidos.

---

## 4. Fragments: navegação pelo grafo

### `Ui/SearchFragment.kt`
- `parentFragmentManager.commit { replace(...); addToBackStack(null) }` →
  `findNavController().navigate(R.id.action_searchFragment_to_pokemonDetailFragment, args)`.
- **Proteção:** a navegação só acontece se a tela atual ainda for a Busca. A busca navega
  depois de um `postDelayed` e do pré-carregamento do sprite, então um callback atrasado
  ou um clique duplo poderia tentar navegar duas vezes e travar o app.

### `Ui/GridFragment.kt`
- Mesma troca, usando `action_gridFragment_to_pokemonDetailFragment` e a mesma proteção.

### `Ui/PokemonDetailFragment.kt`
- **Retorno pelo grafo:** o botão "Retornar" passou de `parentFragmentManager.popBackStack()` para
  `findNavController().navigateUp()`.
- **Argumento:** a chave mudou de `"arg_pokemon"` para `"pokemon"`, o mesmo nome do `<argument>` no grafo.
- `newInstance()` foi trocado por `args(pokemon)`, que só monta o `Bundle` de argumentos.
  Quem cria o fragment agora é o NavController.
  Também saiu o `bundleOf`, que está obsoleto, no lugar de `Bundle().apply { putSerializable(...) }`.
- **Snackbar (requisito do PDF):** ao tocar no coração aparece
  *"Charizard adicionado aos favoritos"* ou *"Charizard removido dos favoritos"*, com o botão
  **Desfazer**, que volta ao estado anterior. É o mesmo comportamento do exemplo "UNDO" do PDF.
- O estado do coração vem do `DetailViewModel` (MVVM), que escuta o Firestore.
  Antes o fragment consultava o Room diretamente.

### `Ui/FavoritesFragment.kt` — reescrito
- **AlertDialog (requisito do PDF):** tocar no coração de um item abre
  *"Remover favorito — Tem certeza que deseja remover X dos seus favoritos?"* com três botões:
  - **Sim**: remove do Firebase.
  - **Não**: fecha o diálogo.
  - **Ver detalhes**: é o botão neutro, como o "NEUTRO" do exemplo do PDF, e abre o detalhe.
  Usa `MaterialAlertDialogBuilder`, que segue o tema Material 3 do app.
- A lista vem do `FavoritesViewModel` e **se atualiza sozinha**, porque o listener do Firestore avisa a cada mudança.
  O `onResume()` que recarregava a lista não é mais necessário.
- O clique em um item vai para o detalhe por `action_favoritesFragment_to_pokemonDetailFragment`.
  A busca do Pokémon na API saiu do fragment e foi para o ViewModel.
- **Botão "Sair" (novo, não está no PDF):** sem logout não há como trocar de usuário,
  o que o requisito "favoritos de acordo com o usuário" pressupõe. Ele faz logout,
  volta ao login pela ação global e descarta as pilhas salvas das abas.

### `res/layout/fragment_favorites.xml`
- **Adicionado** o botão de texto "Sair" no canto superior direito, alinhado ao título.
- **Corrigido:** o `RecyclerView` tinha altura `wrap_content` sem restrição embaixo. Com muitos
  favoritos ele passava do fim da tela e não rolava direito. Agora é `0dp`, preso ao fim do layout.

---

## 5. Login e cadastro (Firebase Authentication)

### `Ui/LoginFragment.kt` + `res/layout/fragment_login.xml` — novos
- Layout do mockup: pokébola, campos **E-mail** e **Senha**, botão **Entrar**, **Esqueci a senha** e **Cadastre-se**.
- **Entrar:** faz login com e-mail e senha no Firebase e vai para a Busca pela seta do grafo.
- **Esqueci a senha:** envia o e-mail de redefinição do Firebase para o endereço digitado no campo.
- **Cadastre-se:** abre o cadastro pela seta `action_loginFragment_to_registerFragment`.
- Erros aparecem em Snackbar, com textos do `strings.xml`.
- Envolvido em `ScrollView`, para rolar quando o teclado abre.

### `Ui/RegisterFragment.kt` + `res/layout/fragment_register.xml` — novos
- Layout do mockup: seta de voltar, título **Cadastro**, campos **Nome**, **Nick**, **E-mail**,
  **Senha** e **Confirme a senha**, e o botão **Cadastrar**.
- A seta de voltar usa `navigateUp()`, ou seja, o retorno é pelo grafo.
- Validações antes de chamar o Firebase: campos vazios, e-mail inválido,
  senha com menos de 6 caracteres (mínimo do Firebase) e senhas diferentes.
- Depois do cadastro o Firebase já deixa o usuário logado, e o app vai direto para a Busca.

### `ViewModel/LoginViewModel.kt`, `ViewModel/RegisterViewModel.kt` — novos
- Guardam o estado `carregando`, a `mensagem` de erro e o evento de sucesso em `LiveData`.
  Os fragments só observam (MVVM).

### `ViewModel/AuthErrors.kt` — novo
- Traduz as exceções do Firebase em mensagens em português: senha fraca, e-mail já cadastrado,
  login inválido e sem conexão.

### `Data/remote/AuthRepository.kt` — novo
- Concentra `login`, `register`, `sendPasswordReset`, `logout` e `isLoggedIn`.
- No cadastro, grava o **nick** como `displayName` do usuário e salva `{name, nick, email}`
  em `users/{uid}` no Firestore.

---

## 6. Favoritos no Firebase por usuário (requisito destacado no PDF)

### Removidos: `Data/local/AppDatabase.kt` e `Data/local/FavoriteDAO.kt`
- O banco Room local deixou de existir. Os favoritos agora ficam na nuvem.

### `Data/local/FavoriteModel.kt` → `Data/remote/FavoriteModel.kt`
- Movido para o pacote `remote` e transformado em `data class` simples, sem as anotações do Room.
- Os valores padrão (`id = 0`, `name = ""`…) dão o construtor vazio que o Firestore exige para converter documentos.

### `Data/local/FavoritesRepository.kt` → `Data/remote/FavoritesRepository.kt` — reescrito
- Estrutura no Firestore:
  ```
  users/{uid}                       ← perfil (nome, nick, e-mail)
  users/{uid}/favorites/{pokemonId} ← { id, name, imageUrl }
  ```
  Cada usuário tem a **sua própria** coleção de favoritos.
- `observeAll()` e `observeIsFavorite()` usam *snapshot listeners*, então a interface se atualiza sozinha.
- `add()` e `remove()` gravam e apagam o documento do Pokémon.
- O Firestore tem cache offline. A tela atualiza na hora, mesmo antes da resposta do servidor.

### `ViewModel/FavoritesViewModel.kt`, `ViewModel/DetailViewModel.kt` — novos
- Fazem a ponte entre o repositório e as telas por `LiveData`, e removem os listeners no `onCleared()`.

### `Ui/Adapter/FavoritesAdapter.kt`, `Ui/ViewHolder/FavoriteViewHolder.kt`
- Só o import mudou: `Data.local.FavoriteModel` → `Data.remote.FavoriteModel`.

---

## 7. `res/values/strings.xml`
Foram adicionados todos os textos novos, cumprindo o requisito "uso obrigatório de strings.xml":
os títulos do menu, os campos de login e cadastro, as mensagens de erro, a Snackbar
(`%1$s adicionado aos favoritos`, `Desfazer`) e o diálogo (`Remover favorito`, `Sim`, `Não`, `Ver detalhes`).

---

## ⚠️ O que você precisa fazer para o Firebase funcionar

O código está pronto, mas o app precisa do arquivo de configuração **do seu** projeto Firebase.
Sem ele o **build falha** com *"File google-services.json is missing"*.

1. Acesse <https://console.firebase.google.com>, crie um projeto e adicione um **app Android** com o
   pacote `com.example.pokedex`.
2. Baixe o **`google-services.json`** e coloque em `app/google-services.json`.
3. Em **Authentication → Sign-in method**, ative **E-mail/senha**.
4. Em **Firestore Database**, crie o banco e use estas regras, para que cada usuário só leia e grave os próprios dados:
   ```
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /users/{uid}/{document=**} {
         allow read, write: if request.auth != null && request.auth.uid == uid;
       }
     }
   }
   ```

---

## Como foi verificado

- `./gradlew :app:assembleDebug` → **BUILD SUCCESSFUL**. O build usou um `google-services.json`
  **provisório**, apagado logo depois.
- No emulador (Pixel 6a): o app abre direto no **login**, sem a barra inferior.
  **Cadastre-se** abre o cadastro, e a **seta de voltar** retorna ao login pelo grafo. Nenhum crash.
- **Não testado** (depende do seu Firebase): login e cadastro reais, a barra inferior com as abas,
  o Snackbar ao favoritar, o AlertDialog dos favoritos, a gravação no Firestore e o logout.
  Depois de adicionar o `google-services.json`, teste esses fluxos.
