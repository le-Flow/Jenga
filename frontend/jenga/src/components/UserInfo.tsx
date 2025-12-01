import { Stack } from "@suid/material"
import { LoginResponseDTO } from "../api/generated"

interface UserInfoProps {
    user: LoginResponseDTO
}

export const UserInfo = (props: UserInfoProps) => {
    return (
        <Stack>
            <span>{props.user.username}</span>
{/*             <span>{props.user.email}</span> */}
        </Stack>
    )
}