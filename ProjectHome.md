## What's gobo-tools? ##
Tools for developers using Google App Engine.
You can dump data on GAE datastore to Google Spreadsheet,
and also can restore data on Google Spreadsheet to GAE datastore.

You can install this to any existing java project.

### Dump ###
> ![http://img.f.hatena.ne.jp/images/fotolife/k/knj77/20101006/20101006232815.png](http://img.f.hatena.ne.jp/images/fotolife/k/knj77/20101006/20101006232815.png)


> _example_
> > ![http://img.f.hatena.ne.jp/images/fotolife/k/knj77/20101010/20101010232825.png](http://img.f.hatena.ne.jp/images/fotolife/k/knj77/20101010/20101010232825.png)


### Restore ###

> ![http://img.f.hatena.ne.jp/images/fotolife/k/knj77/20101006/20101006232816.png](http://img.f.hatena.ne.jp/images/fotolife/k/knj77/20101006/20101006232816.png)


### Drop (kinds) ###
Additionally, You can use Drop (kinds) tool.

### Demo ###
http://gobo-tools.appspot.com

Please don't drop:)


### Use case ###
  * You can edit the data on Google Spreadsheet manually or by Apps Script.
  * You can share the data among the team's members.
  * (From Google Spreadsheet) You can download/upload as a Microsoft Excel file.
etc... and restore the data back to GAE.


## How to install ##
http://code.google.com/p/gobo-tools/wiki/HowToInstall


## Notice ##
  * This uses AuthSub to write/read Google Spreadsheet.
  * This tool can not take a snapshot of datastore. This extracts data little by little using task queue and cursor.
  * Because of the spreadsheet api's problem, dumping many kinds at once doesn't work well...
  * This doesn't support Blob and ShortBlob, and You can't dump Text from dev\_environment because of SDK's problem.
  * A kind named '_GOBO\_CONTROL_' is made by this tool for cotrolling.(It is removed When all the tasks are finished.)
  * This refers "Datastore Statistics" on production.
  * The restore might destroy your important data. Please verify before using at production!