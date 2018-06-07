<p>
    <h1> Matter for Reddit</h1>
    <b> (because it is made of "material") </b>
</p>

<p>This is mostly a playground for me to prototype out some Android libraries but eventually it'll turn into something release-able (I hope).</p>

<p>I believe in open source but I also believe in money. This entire portion you see in the code will be free to pull and use, but the repo will contain "secret hooks" that hook into a proprietary repo for additional paid features. You can pull and build this all you want, but it's just Reddit if you do.</p>

<p>
    <h2>Build</h2>
    <p>My client secret, username, and redirect URL are hidden keys I pass in to Gradle. To pass your own in to build simple specify them:</p>
    <p>gradle build -PredditClientId={clientId} -PredditUsername={username} -PredditRedirectUrl={redirectUrl}</p>
</p>