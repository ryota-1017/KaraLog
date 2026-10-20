# KaraLog — Minimal Karaoke Assistant

![Platform](https://img.shields.io/badge/Platform-Android-171717?style=flat-square)
![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-171717?style=flat-square)
![Design](https://img.shields.io/badge/Design-Contemporary%20Monotone-171717?style=flat-square)
![Storage](https://img.shields.io/badge/Storage-Room%20(Local%20Only)-171717?style=flat-square)

> **「自分だけのカラオケ選曲データベース ＆ 現場特化型アシスタント」**  
> 装飾を極限まで削ぎ落とした、Instagram / Threads スタイルの静謐で都会的なカラオケ記録アプリ。  
> 完全ローカル保存（SQLite / Room DB）により、通信費ゼロ・サーバー代0円・完全プライベートで利用できます。

---

## 🌟 特徴とデザイン仕様

### 1. 📐 Instagram × Threads スタイルのコンテンポラリーデザイン
- **UIの気配を極限まで消した実用美**: カードの枠線やドロップシャドウを排除し、余白の緩急とタイポグラフィの階層だけで整然と表現。
- **厳格なモノトーンパレット**: `#FFFFFF`, `#FAFAFA`, `#E5E5E5`, `#171717`, `#737373` のみで統一。原色や余計なノイズを一切排した大人のデザイン。
- **端正なアイコン ＆ フルワイズ・ボトムバー**: 文字ラベルのない細線アイコンのみが並ぶ、洗練された下部ナビゲーション。
- **Edge-to-Edge 完全対応**: ステータスバー（時計・カメラノッチ）やナビゲーションバーとの重なりを自動解消。

### 2. ⚡ 直感的なホーム画面（手書きスケッチの完全再現）
- **ストーリーズ風 クイックアクショントレイ**:
  - `[🔍 検索]` `[🎤 歌唱]` `[🕒 履歴]` `[➕ 追加]` が円形サークルで並び、ワンタップでアクセス可能。
- **オススメ曲リスト**:
  - 正方形のミニマルサムネイル、曲名、アーティスト名、オプションメニュー（`⋮`）をタイトに配置。
- **過去のランキング**:
  - 順位バッジ（`1.`）、アルバムアート風サムネイル、歌唱日付、機種別スコア（`98.1 精密採点AI`）を一目で確認。

### 3. 🎯 カラオケ現場に特化した実用機能
- **キー・予約番号メモ**: 曲ごとに自分に合ったキー（`±0`, `+2`, `-1`）やデンモク予約番号を記録。
- **スマートテンキー採点入力**: `94821` と打つだけで自動的に `94.821` にフォーマットされる快速入力。
- **DAM / JOYSOUND 対応フラグ**: 機種ごとの配信状況や採点結果をスマートに管理。

### 4. 🔒 完全ローカル・プライバシー保護
- すべてのデータはお手元の端末内（Room SQLite）にのみ保存されます。
- アカウント登録不要、ログイン不要、外部通信なし。
- 電波の届きにくい地下のカラオケボックスでも爆速・快適に動作します。

---

## 📲 ダウンロード（Obtainium 対応）
GitHub Releases にて最新の APK（`app-debug.apk`）を配信しています。  
[Obtainium](https://github.com/ImranR98/Obtainium) に本リポジトリの URL を登録することで、ワンタップでインストールおよび自動更新が可能です。

---

## 🛠️ ビルド方法
```bash
./gradlew assembleDebug
```
生成物: `app/build/outputs/apk/debug/app-debug.apk`