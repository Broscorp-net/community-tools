import sys

from pr_agent import cli
from pr_agent.config_loader import get_settings
from pr_agent.algo import MAX_TOKENS

def get_prompt_based_on_task(pr_url):
    common_prompt = """The code suggestions should focus only on the following:
                       - Code should be readable
                       - Avoid many nested loops and ifs
                       - No debug code(like printing into the console) or unused methods/fields
                       - Ensure efficient use of collections
                       - Variables names should be understandable, descriptive and concise"""

    game_of_life_prompt = common_prompt + """
                                               - Single-responsibility principle
                                               - Exception must be thrown if file not found
                                               - Don't locate all the logic in a huge try/catch
                                               - Use try with resources when working with files
                                               - There shouldn't be files in pull request like outputGlider.txt etc
                                               - Focus on algorithm efficiency
                                               - Shouldn't be the use of unnecessary techniques that add complexity without significant benefits like threads"""

    gc_impl_prompt = common_prompt + """ - It shouldn't be any other classes except GarbageCollectorImplementation.java` in pull request"""

    if "gc-implementation" in pr_url:
        return gc_impl_prompt
    elif "game-of-life" in pr_url:
        return game_of_life_prompt
    else:
        return common_prompt


def main(pr_url):
    provider = "github"
    user_token = str(sys.argv[2])
    openai_key = str(sys.argv[3])
    api_type = "azure"
    api_version = '2024-02-01'
    api_base = str(sys.argv[4])
    deployment_id = "gpt-4o-mini"
    model = "gpt-4o-mini"
    command = "/improve"
    num_code_suggestions = 10

    prompt = get_prompt_based_on_task(pr_url)

    get_settings().set("CONFIG.git_provider", provider)
    get_settings().set("openai.key", openai_key)
    get_settings().set("github.user_token", user_token)
    get_settings().set("openai.api_type", api_type)
    get_settings().set("openai.api_version", api_version)
    get_settings().set("openai.deployment_id", deployment_id)
    get_settings().set("openai.api_base", api_base)
    get_settings().set("config.model", model)
    get_settings().set("pr_code_suggestions.extra_instructions", prompt)
    get_settings().set("pr_code_suggestions.num_code_suggestions", num_code_suggestions)

    cli.run_command(pr_url, command)


if __name__ == '__main__':
    pr_link = str(sys.argv[1])
    main(pr_link)
