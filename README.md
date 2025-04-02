# 🎶 Jukebox

Uma Jukebox moderna feita em Java + Swing, com suporte a reprodução de músicas `.mp3`, exibição de capas de álbuns e interface inspirada em sistemas clássicos de som — tudo com muito estilo. 😎🎧

---

## ✨ Funcionalidades

- 🎵 Leitura automática de músicas `.mp3` de uma pasta local
- 📂 Seleção de pasta personalizada via interface
- 🖼️ Exibição automática da capa do álbum (embedded no MP3)
- ▶️ Botões funcionais de **Play**, **Pause** e **Stop**
- 🎛️ Interface Swing intuitiva e retrô
- 💾 Totalmente empacotável em `.jar` executável

---

## 🛠️ Tecnologias utilizadas

- [Java 17+](https://openjdk.org)
- [Swing](https://docs.oracle.com/javase/8/docs/technotes/guides/swing/)
- [Maven](https://maven.apache.org/)
- [JLayer](http://www.javazoom.net/javalayer/javalayer.html) – reprodução de MP3
- [Jaudiotagger](https://bitbucket.org/ijabz/jaudiotagger) – leitura de metadados (capa)

---

## 🧪 Como rodar o projeto

### 1. Clone o repositório:
```bash
  git clone https://github.com/andregnicoletti/jukebox.git
  cd jukebox
```

### 2. Compile com Maven:
```bash
  mvn clean package
```

### 3. Execute a aplicação:
```bash
  java -jar target/jukebox-swing-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## 📁 Organização do projeto
```bash
    src/
     ├── main/
     │    ├── java/
     │    │    └── br/com/jukebox/
     │    │         ├── JukeboxApp.java
     │    │         ├── ui/
     │    │         ├── audio/
     │    │         └── service/
     │    └── resources/
     │         └── default-cover.png
```

## 📌 Observações
- O projeto não inclui músicas por questões legais.
- Arquivos de música devem ser .mp3 com metadados válidos para capa.
- Capas padrão estão em /resources

## 💡 Próximos passos
- 🔁 Barra de progresso da música
- 🎚️ Controle de volume
- 🎨 Suporte a temas visuais
- 🧠 Playlist inteligente (modo aleatório, favoritos)

## 🤝 Contribuições
Sinta-se à vontade para contribuir com ideias, sugestões, melhorias ou bugs.
Pull requests são muito bem-vindos! 🚀
