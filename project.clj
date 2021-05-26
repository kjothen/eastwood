(def eval-in-leiningen?
  (#{"1" "true"} (System/getenv "EVAL_IN_LEININGEN")))

(def plugin-source-path "lein-eastwood")

(defproject jonase/eastwood "0.4.3"
  :description "A Clojure lint tool"
  :url "https://github.com/jonase/eastwood"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ~(cond-> ["src" "copied-deps"]
                   eval-in-leiningen? (conj plugin-source-path))
  :dependencies [[org.clojure/clojure "1.10.3" :scope "provided"]
                 [org.clojars.brenton/google-diff-match-patch "0.1"]
                 [org.ow2.asm/asm-all "5.2"]]
  :deploy-repositories [["clojars" {:url "https://repo.clojars.org"
                                    :username :env/clojars_username
                                    :password :env/clojars_password
                                    :sign-releases true}]]
  :profiles {:dev {:dependencies [[org.clojure/tools.macro "0.1.5"]
                                  [jafingerhut/dolly "0.1.0"]]}
             :eastwood-plugin {:source-paths [~plugin-source-path]
                               :jvm-opts ["-Deastwood.internal.plugin-profile-active=true"]}
             :warn-on-reflection {:global-vars {*warn-on-reflection* true}}
             :test {:dependencies
                    ;; NOTE: please don't add non-essential 3rd-party deps here.
                    ;; It is desirable to rest assured that the test suite does not depend on a third-party lib.
                    ;; If you wish to exercise compatibility against a 3rd-party lib, use the `:test-3rd-party-deps` profile instead.
                    [[commons-io "2.4" #_"Needed for issue-173-test"]]
                    :resource-paths ["test-resources"
                                     ;; if wanting the `cases` to be available during development / the default profile,
                                     ;; please simply add `with-profile +test` to your CLI invocation.
                                     "cases"]
                    :jvm-opts ["-Deastwood.internal.running-test-suite=true"]}
             :test-3rd-party-deps {:test-paths ^:replace ["test-third-party-deps"]
                                   :dependencies [[com.nedap.staffing-solutions/speced.def "2.0.0"]
                                                  [com.taoensso/timbre "5.1.2"]
                                                  [com.taoensso/tufte "2.2.0"]
                                                  [manifold "0.1.9-alpha4"]]}
             :1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :1.8 {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :1.9 {:dependencies [[org.clojure/clojure "1.9.0"]]}
             :1.10.1 {:dependencies [[org.clojure/clojure "1.10.1"]]}
             :1.10.2 {:dependencies [[org.clojure/clojure "1.10.2"]]}
             :1.10.3 {:dependencies [[org.clojure/clojure "1.10.3"]]}
             ;; NOTE: if adding a new Clojure version here, please be sure var-info.edn remains up-to-date.
             ;; you can use the `:check-var-info` Lein profile for that.

             ;; the `:check-var-info` profile helps keeping the var-info.edn up to date. You can exercise it with:
             ;; lein with-profile -user,-dev,+eastwood-plugin,+check-var-info eastwood '{:debug [:var-info]}'
             ;; ...among other things, it will print stubs of the missing entries so that one can copy/paste them (after due adaptations)
             :check-var-info {:dependencies [;; Latest as of May 25 2021
                                             ;; (copied from `crucible/check-var-info`)
                                             ;; algo.generic
                                             ;; algo.graph
                                             ;; cheshire
                                             ;; clj-http
                                             ;; compojure
                                             [org.clojure/core.async "1.3.618"]
                                             [org.clojure/core.cache "1.0.207"]
                                             [org.clojure/core.memoize "1.0.236"]
                                             [org.clojure/data.codec "0.1.1"]
                                             [org.clojure/data.csv "1.0.0"]
                                             [org.clojure/data.json "2.3.1"]
                                             [org.clojure/data.priority-map "1.0.0"]
                                             ;; data.xml
                                             ;; data.zip
                                             ;; loom?
                                             ;; instaparse
                                             [org.clojure/java.jdbc "0.7.12"]
                                             ;; math.combinatorics
                                             [org.clojure/math.numeric-tower "0.0.4"]
                                             ;; medley
                                             ;; plumbing
                                             ;; potemkin
                                             ;; ring
                                             ;; schema
                                             ;; timbre
                                             ;; tools.analyzer
                                             ;; tools.analyzer.jvm
                                             ;; tools.cli
                                             ;; tools.logging
                                             ;; tools.macro
                                             ;; tools.namespace
                                             ;; tools.nrepl
                                             [org.clojure/tools.reader "1.3.4"]
                                             [org.clojure/tools.trace "0.7.11"]
                                             ;; useful
                                             ]}
             :var-info-test {:test-paths ^:replace ["var-info-test"]}}
  :aliases {"test-all" ["with-profile"
                        ~(->> ["1.7" "1.8" "1.9" "1.10.1" "1.10.2" "1.10.3"]
                              (map (partial str "-user,-dev,+test,+warn-on-reflection,+"))
                              (clojure.string/join ":"))
                        "test"]}
  :eastwood {:source-paths ["src"]
             :test-paths ["test"]
             :debug #{}}
  :plugins [[net.assum/lein-ver "1.2.0"]]
  :lein-ver {:version-file "resources/EASTWOOD_VERSION"}
  ;; Eastwood may work with earlier Leiningen versions, but this is
  ;; close to the earliest version that it was most tested with.
  :min-lein-version "2.3.0"
  :resource-paths ["resource" "resources"]
  :eval-in ~(if eval-in-leiningen?
              :leiningen
              :subprocess))
