import { Card, CardContent, CardHeader, Link, Stack, Typography } from "@suid/material"
import { useContext } from "solid-js"
import { I18nContext } from "../provider/I18nProvider"

export const About = () => {
    const i18n = useContext(I18nContext)

    return (
        <Card>
            <CardHeader title={i18n?.t("pages.about.title")}></CardHeader>
            <CardContent>
                <Stack spacing={2}>
                    <Typography variant="body1">{i18n?.t("pages.about.description")}</Typography>
                    <Typography variant="body2" color="text.secondary">{i18n?.t("pages.about.earlyDevelopment")}</Typography>
                    <Stack direction="row" spacing={2} flexWrap="wrap">
                        <Link href="https://github.com/Jenga-PMS/Jenga" target="_blank" rel="noreferrer">
                            {i18n?.t("pages.about.links.sourceCode")}
                        </Link>
                        <Link href="https://jenga-pms.github.io/" target="_blank" rel="noreferrer">
                            {i18n?.t("pages.about.links.documentation")}
                        </Link>
                    </Stack>
                </Stack>
            </CardContent>
        </Card>
    )
}
