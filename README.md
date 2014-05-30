# socket-rocket

A [LogStash](http://logstash.net/) appender for [timbre](https://github.com/ptaoussanis/timbre).

This will send json through a socket to your logstash listener.

## Usage
Timbre  Configuration:

```clojure
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

## License

Copyright © 2014 Flybe

Distributed under the Eclipse Public License v1.0.