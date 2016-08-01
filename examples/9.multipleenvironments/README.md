This is an example of using one script in two environments.

The important settings here are these settings in the jnlp file:

```
    <property name="javaws.featureGroupName" value="CQS-SEC"/>
```

and

```
    <property name="javaws.featureGroupName" value="CQS-ECOM"/>
```

and these settings in the configuration.xml file:

```
        <featureGroup name="CQS-ECOM">
            <urlMapping>
                <url name="default">https://secure.budgetdirect.com.au/car/new_quote.jsp?hSty=BUDD</url>
            </urlMapping>
        </featureGroup>
		<featureGroup name="CQS-SEC">
			<urlMapping>
				<url name="default">https://secure.budgetdirect.com.au/car/new_quote.jsp?hSty=BUDD</url>
			</urlMapping>
		</featureGroup>
```