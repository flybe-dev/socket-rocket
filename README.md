# socket-rocket

A [LogStash](http://logstash.net/) appender for [timbre](https://github.com/ptaoussanis/timbre).

This will send json through a socket to your logstash listener.

##Dependencies
###Leiningen
```[com.flybe/socket-rocket "0.1.0"]```
###Gradle
```compile "com.flybe:socket-rocket:0.1.0"```
###Maven
```xml
<dependency>
  <groupId>com.flybe</groupId>
  <artifactId>socket-rocket</artifactId>
  <version>0.1.0</version>
</dependency>
```

## Usage
Timbre  Configuration:

```clojure
(require '[socket-rocket.logstash :refer (logstash-appender make-logstash-appender)])

(timbre/set-config! [:appenders :logstash] logstash-appender)
(timbre/set-config! [:shared-appender-config :logstash] {:port 4660 :logstash "192.168.0.2"})
```

LogStash Server configuration:
```ruby
input {
	tcp {
	data_timeout => 5
	host => "0.0.0.0"
	port => 4660
	mode => "server"
	type => "clojure"
	} 
}

filter {
	if [type] == "clojure" {
		json {
			source => "message"
		}
	}
}

output { 
    elasticsearch { 
        host => localhost 
		}	
}
```

## Customization

You can specify a custom formatter for transforming timbre log events into messages to
logstash. Default formatter used by the `logstash-appender` looks like this:

```clojure
(defn json-formatter
  [{:keys [level throwable timestamp message hostname args] :as params}]
  (generate-string
    {:level      level
     :throwable  (timbre/stacktrace throwable)
     :msg        message
     :timestamp  (-> timestamp strs/upper-case)
     :hostname   (-> hostname strs/upper-case)
     :ns         (str *ns*)}))
```

But you can specify your own function, `my-json-formatter` and override the
default appender in the config like so:

```clojure
(timbre/set-config! [:appenders :logstash] (make-logstash-appender my-json-formatter))
```

## License

Copyright © 2014 Flybe

Distributed under the Eclipse Public License v1.0.
