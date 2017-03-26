# scala-quiz
Scalaで書いてみよう!

## 遊び方
1. `challenge` パッケージ以下各クラスの `???` 部分を実装します。
2. 例えばC1_List_Optionの問題を解答した場合は
    ```sh
    $ sbt
    > test-only challenge.chapter1.C1_List_Option
    ```
    のように実行します。
3. テストケースは `question` パッケージ以下にあります。
4. 解答例は `answer` パッケージ以下にあります。