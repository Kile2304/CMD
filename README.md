# CMTerm

Questo progetto è un JFrame che emula un terminale. Questo widget ti consente di creare
comandi personalizzati o salvare scorciatoie per comandi. Ad esempio, eseguendo il comando:
```shell
set to_replace "mvn clean package"
```
Verrà creata una scorciatoia che potrai richiamare in questo modo:
```shell
${to_replace}
```
---

## Come usare il Frame del Terminale
Per usare questo frame, richiama semplicemente il comando: 
TerminalFrame(false, sessionKey) Setting false indica che il terminale non è in 
modalità di sola lettura.

## Main di esempio

```kotlin
fun main() {
    installTheme("/themes/Dracula.theme.json")
    RuntimeCommand.COMMANDS.entries
    UIHandler.setDefaultCMD(CliNow::class.java)
    SwingUtilities.invokeLater {
        UIHandler.newCMDRequest("${System.currentTimeMillis()}", null)?.apply {
            isVisible = true
        }
    }
    println("here")
}
```

## Come creare comandi
Per creare un comando è necessario creare 1 classe che identifica 
i campi del comando, e un'altro che lo esegue:
Comando:
```kotlin
// Definisce un nuovo comando
@Command(
    // Sul terminale verranno cercati comandi che si chiamano "color"
    name = "COLOR"
    , classExecutor = ColorRunner::class    // Classe che esegue il comando
    , description = "Allow you to change the color of your console" // Descrizione per l'helper
)
class ColorCommand : BaseArgument() {

    @Parameter(
        // Nome per l'helper
        name = "COLOR"
        , index = 0 // Indica che il primo parametro inserito è il seguente
        , // Prima di controllare per posizione viene cercato per indice
        // e' anche possibile specificare il parametro con -color e -c
        names = [ "color", "c" ]
        , required = true   // Se non specificato ritorna errore
        , description = "The color" // Descrizione per l'helper
    )
    var color: String = ""
        private set

    @Parameter(
        name = "FOREGROUND"
        , index = 0
        , names = [ "foreground", "f" ]
        , hasArguments = false  //Boolean, quindi, non ha bisogno di arguments
        , description = "Means that will be changed the color of the foreground"
    )
    var isForeground: Boolean = false
        private set

    @Parameter(
        name = "BACKGROUND"
        , index = 0
        , names = [ "background", "b" ]
        , hasArguments = false
        , description = "Means that will be changed the color of the background"
    )
    var isBackground: Boolean = false
        private set

}
```
Runner:
```kotlin
class ColorRunner: CommandRunner() {
    override fun execute(arg: BaseArgument, sessionKey: String) {
        UIHandler.getFrameFromSession(sessionKey)?.let {
            // Cambia il colore
        }
    }
}
```

## Caratteristiche
- Supporto per terminali multi-tab e single-tab
- Possibilità di spostare i tab tra i vari frame del terminale
- Possibilità di rinominare terminali o tab attraverso il comando title
- Supporto di molti comandi cmd
- Supporto di colorazioni tramite ansi sequence
- Supporto di alcune ansi action
- Hotkey per la navigazione del terminale
- Possibilità di creare dei comandi custom in kotlin

## Funzionalità in arrivo
- Comando per eseguire query SQL direttamente dal terminale
- Comando personalizzato per creare hotkey (richiederà l'uso di un'altra libreria)
- Supporto di ulteriori ansi action
- Supporto di ulteriori comandi cmd
- Supporto Powershell
- Supporto Shell

## Contribuire
Gli contributi sono i benvenuti! Sentiti libero di aprire una issue o una pull request.

## License
Questo progetto è concesso in licenza sotto MIT.