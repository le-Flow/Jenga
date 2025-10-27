import { Card, CardContent, CardHeader } from "@suid/material"
import { Projects } from "../components/Projects"

export const Home = () => {
    return (
        <Card>
            <CardHeader title="Home"></CardHeader>
            <CardContent>
                <Projects></Projects>
            </CardContent>
        </Card>
    )
}