package it.cm.cmd.core.ui.cli.panel

import it.cm.cmd.core.ui.listener.ICmd
import javax.swing.JPanel

class CLIBottomPanel(
    private val iCmd: ICmd
) : JPanel() {

//    init {
//        isOpaque = false;
//        layout = GridLayout(1, 1)
//
//        add(textField())
//    }
//
//    fun textField() =
//        JTextField().apply {
//            caretColor = Color.GREEN
//            size = Dimension(CMD_DIMENSION.width, 30)
//            autoscrolls = true
//            background = CMD_BACKGROUND
//            foreground = Color.YELLOW
//            focusTraversalKeysEnabled = false
//
//            addKeyListener(CmdFieldListener(iCmd))
//            val autoComplete = Autocomplete(this, getAutocompleteKeywords())
//            document.addDocumentListener(autoComplete)
//            inputMap.put(KeyStroke.getKeyStroke("TAB"), "commit")
//            actionMap.put("commit", autoComplete.CommitAction())
//        }
//
//    private fun getAutocompleteKeywords(): List<String> {
//        val keywords: MutableList<String> = ArrayList<String>(11).apply {
//            add("select")
//            add("from")
//            add("inner join")
//            add("left join")
//            add("where")
//            add("having")
//            add("count(*)")
//            add("order by")
//            add("group by")
//            add("desc")
//            add("asc")
//        }
////        if (Loaded.configuration.isEnabledDBShortcutOnCMD) {
////            val schemas: List<ISchema> = Loaded.getMainService(null).getSchemas(null)
////            keywords.addAll(
////                schemas.stream()
////                    .flatMap(Function<ISchema, Stream<*>> { schema: ISchema ->
////                        schema.getTables().stream()
////                    })
////                    .map<Any>(ITable::getName)
////                    .collect(Collectors.toList())
////            )
////        }
//        return keywords
//    }

}