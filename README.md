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
  git clone https://github.com/seuusuario/jukebox.git
  cd jukebox
```

```bash
  mvn clean package
```

```bash
  java -jar target/jukebox-swing-1.0-SNAPSHOT-jar-with-dependencies.jar
```